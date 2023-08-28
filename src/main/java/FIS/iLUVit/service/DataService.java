package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Region;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.ClassInfo;
import FIS.iLUVit.domain.embeddable.TeacherInfo;
import FIS.iLUVit.dto.data.*;
import FIS.iLUVit.exception.DataErrorResult;
import FIS.iLUVit.exception.DataException;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DataService {

    private final RegionRepository regionRepository;
    private final CenterRepository centerRepository;

    @Value("${center.api-key.child-house}")
    private String childHouseSecretKey;


    @Value("${center.api-key.kindergarten}")
    private String kindergartenSecretKey;

    /**
     * 어린이집 정보를 업데이트합니다
     */
    @Transactional
    public void updateChildHouseInfo() {
        List<Region> regionList = regionRepository.findAll();

        for (Region region : regionList) {
            List<ChildHouseInfoResponse> responses = getChildHouseInfo(region.getSigunguCode());

            for(ChildHouseInfoResponse response : responses) {
                List<Center> centerList = centerRepository.findByNameAndAreaSidoAndAreaSigungu(
                        response.getCenterName(), response.getArea().getSido(), response.getArea().getSigungu());

                if (centerList.size() == 1) {
                    Center center = centerList.get(0);
                    center.updateCenter(response);
                }
            }
        }
    }

    /**
     * 유치원 정보를 업데이트합니다
     */
    public void updateKindergartenInfo() {
        List<Region> regionList = regionRepository.findAll();

        for (Region region : regionList) {
            updateKindergartenGeneralInfo(region);
            updateKindergartenTeacherInfo(region);
            updateKindergartenSafetyInfo(region);
            updateKindergartenBuildingInfo(region);
            updateKindergartenBuildingInfo(region);
            updateKindergartenSchoolBusInfo(region);
        }
    }

    /**
     * 유치원 일반현황 정보를 업데이트합니다
     */
    private void updateKindergartenGeneralInfo(Region region) {

        List<KindergartenGeneralResponse> generalResponseList = getKindergartenGeneralInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenGeneralResponse generalResponse : generalResponseList) {
            List<Center> centerList = centerRepository.findByNameAndAreaSidoAndAreaSigungu(
                    generalResponse.getCenterName(), region.getSidoName(), region.getSigunguName());

            if (centerList.size() == 1) {
                Center center = centerList.get(0);
                center.updateCenter(generalResponse);
            }
        }
    }

    /**
     * 유치원 근속연수현황 정보를 업데이트합니다
     */
    private void updateKindergartenTeacherInfo(Region region) {

        List<KindergartenTeacherResponse> teacherResponseList = getKindergartenTeacherInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenTeacherResponse teacherResponse : teacherResponseList) {
            List<Center> centerList = centerRepository.findByNameAndAreaSidoAndAreaSigungu(
                    teacherResponse.getCenterName(), region.getSidoName(), region.getSigunguName());

            if (centerList.size() == 1) {
                Center center = centerList.get(0);
                center.updateCenter(teacherResponse);
            }
        }
    }

    /**
     * 유치원 통학차량현황 정보를 업데이트합니다
     */
    private void updateKindergartenSchoolBusInfo(Region region) {

        List<KindergartenBasicInfraResponse> schoolBusResponseList = getKindergartenSchoolBusInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenBasicInfraResponse schoolResponse : schoolBusResponseList) {
            List<Center> centerList = centerRepository.findByNameAndAreaSidoAndAreaSigungu(
                    schoolResponse.getCenterName(), region.getSidoName(), region.getSigunguName());

            if (centerList.size() == 1) {
                Center center = centerList.get(0);
                center.updateCenter(schoolResponse);
            }
        }
    }

    /**
     * 유치원 건물현황 정보를 업데이트합니다
     */
    private void updateKindergartenBuildingInfo(Region region) {

        List<KindergartenBasicInfraResponse> buildingResponseList = getKindergartenBuildingInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenBasicInfraResponse buildingResponse : buildingResponseList) {
            List<Center> centerList = centerRepository.findByNameAndAreaSidoAndAreaSigungu(
                    buildingResponse.getCenterName(), region.getSidoName(), region.getSigunguName());

            if (centerList.size() == 1) {
                Center center = centerList.get(0);
                center.updateCenter(buildingResponse);
            }
        }
    }

    /**
     * 유치원 안전점검ㆍ교육 실시 현황 정보를 업데이트합니다
     */
    private void updateKindergartenSafetyInfo(Region region) {

        List<KindergartenBasicInfraResponse> safetyResponseList = getKindergartenSafetyInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenBasicInfraResponse safetyResponse : safetyResponseList) {
            List<Center> centerList = centerRepository.findByNameAndAreaSidoAndAreaSigungu(
                    safetyResponse.getCenterName(), region.getSidoName(), region.getSigunguName());

            if (centerList.size() == 1) {
                Center center = centerList.get(0);
                center.updateCenter(safetyResponse);
            }
        }
    }

    /**
     * 어린이집 기본 정보 조회 API 호출
     */
    private List<ChildHouseInfoResponse> getChildHouseInfo(String sigunguCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String childHouseGeneralApiUrl = "http://api.childcare.go.kr/mediate/rest/cpmsapi030/cpmsapi030/request";
        String url = childHouseGeneralApiUrl + "?key=" + childHouseSecretKey + "&arcode=" + sigunguCode + "&stcode=";
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        List<ChildHouseInfoResponse> childHouseInfoResponseList = parseXmlResponse(responseEntity.getBody());

        return childHouseInfoResponseList;
    }


    /**
     * xml 포맷데이터를 파싱하여 저장합니다
     */
    private List<ChildHouseInfoResponse> parseXmlResponse(String xmlData) {
        List<ChildHouseInfoResponse> childHouseInfoResponseList = new ArrayList<>();

        try {
            // DocumentBuilderFactory를 생성하여 XML 문서를 파싱할 준비하기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // XML 데이터를 문자열에서 읽어와서 파싱
            Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlData)));

            // XML 문서 구조 정규화
            doc.getDocumentElement().normalize();

            // "item" 태그를 가진 노드들의 목록 가져오기
            NodeList itemNodeList = doc.getElementsByTagName("item");

            // 모든 "item" 노드에 대해서 반복
            for (int i = 0; i < itemNodeList.getLength(); i++) {
                // 현재 반복 중인 "item" 노드 가져오기
                Element itemElement = (Element) itemNodeList.item(i);

                // 기본 정보 관련 노드 가져와서 저장하기
                String centerName = itemElement.getElementsByTagName("crname").item(0).getTextContent();
                String estType = itemElement.getElementsByTagName("crtypename").item(0).getTextContent();
                String status = itemElement.getElementsByTagName("crstatusname").item(0).getTextContent();
                String owner = itemElement.getElementsByTagName("Crrepname").item(0).getTextContent();
                String zipcode = itemElement.getElementsByTagName("zipcode").item(0).getTextContent();
                String homepage = itemElement.getElementsByTagName("crhome").item(0).getTextContent();
                String maxChildCntStr = itemElement.getElementsByTagName("crcapat").item(0).getTextContent();
                String curChildCntStr = itemElement.getElementsByTagName("crchcnt").item(0).getTextContent();
                String program = itemElement.getElementsByTagName("crspec").item(0).getTextContent();

                // Area 관련 노드 가져와서 저장하기
                String sido = itemElement.getElementsByTagName("sidoname").item(0).getTextContent();
                String sigungu = itemElement.getElementsByTagName("sigunguname").item(0).getTextContent();

                // BasicInfra 관련 노드 가져와서 저장하기
                String hasBusStr = itemElement.getElementsByTagName("crcargbname").item(0).getTextContent();
                String hasPlaygroundStr = itemElement.getElementsByTagName("plgrdco").item(0).getTextContent();
                String cctvCntStr = itemElement.getElementsByTagName("cctvinstlcnt").item(0).getTextContent();

                // ClassInfo 관련 노드 가져와서 저장하기
                String class_0_Str = itemElement.getElementsByTagName("class_cnt_00").item(0).getTextContent();
                String class_1_Str = itemElement.getElementsByTagName("class_cnt_01").item(0).getTextContent();
                String class_2_Str = itemElement.getElementsByTagName("class_cnt_02").item(0).getTextContent();
                String class_3_Str = itemElement.getElementsByTagName("class_cnt_03").item(0).getTextContent();
                String class_4_Str = itemElement.getElementsByTagName("class_cnt_04").item(0).getTextContent();
                String class_5_Str = itemElement.getElementsByTagName("class_cnt_05").item(0).getTextContent();
                String child_0_Str = itemElement.getElementsByTagName("child_cnt_00").item(0).getTextContent();
                String child_1_Str = itemElement.getElementsByTagName("child_cnt_01").item(0).getTextContent();
                String child_2_Str = itemElement.getElementsByTagName("child_cnt_02").item(0).getTextContent();
                String child_3_Str = itemElement.getElementsByTagName("child_cnt_03").item(0).getTextContent();
                String child_4_Str = itemElement.getElementsByTagName("child_cnt_04").item(0).getTextContent();
                String child_5_Str = itemElement.getElementsByTagName("child_cnt_05").item(0).getTextContent();
                String child_spe_Str = itemElement.getElementsByTagName("child_cnt_sp").item(0).getTextContent();

                // TeacherInfo 관련 노드 가져와서 저장하기
                String dur_1_Str = itemElement.getElementsByTagName("em_cnt_0y").item(0).getTextContent();
                String dur12_Str = itemElement.getElementsByTagName("em_cnt_1y").item(0).getTextContent();
                String dur24_Str = itemElement.getElementsByTagName("em_cnt_2y").item(0).getTextContent();
                String dur46_Str = itemElement.getElementsByTagName("em_cnt_4y").item(0).getTextContent();
                String dur6_Str = itemElement.getElementsByTagName("em_cnt_6y").item(0).getTextContent();

                // Area Embeddable 객체 생성하고 정보 설정
                Area area = Area.builder()
                        .sido(sido)
                        .sigungu(sigungu)
                        .build();

                // BasicInfra Embeddable 객체 생성하고 정보 전처리 및 설정
                Boolean hasBus = "운영".equals(hasBusStr);
                Boolean hasPlayground = !hasPlaygroundStr.isEmpty();
                Boolean hasCCTV = !cctvCntStr.isEmpty();
                Integer cctvCnt = Integer.parseInt(cctvCntStr);

                BasicInfra basicInfra = BasicInfra.builder()
                        .hasBus(hasBus)
                        .hasPlayground(hasPlayground)
                        .hasCCTV(hasCCTV)
                        .cctvCnt(cctvCnt)
                        .build();

                // ClassInfo Embeddable 객체 생성하고 정보 전처리 및 설정
                Integer class_0 = Integer.parseInt(class_0_Str);
                Integer class_1 = Integer.parseInt(class_1_Str);
                Integer class_2 = Integer.parseInt(class_2_Str);
                Integer class_3 = Integer.parseInt(class_3_Str);
                Integer class_4 = Integer.parseInt(class_4_Str);
                Integer class_5 = Integer.parseInt(class_5_Str);
                Integer child_0 = Integer.parseInt(child_0_Str);
                Integer child_1 = Integer.parseInt(child_1_Str);
                Integer child_2 = Integer.parseInt(child_2_Str);
                Integer child_3 = Integer.parseInt(child_3_Str);
                Integer child_4 = Integer.parseInt(child_4_Str);
                Integer child_5 = Integer.parseInt(child_5_Str);
                Integer child_spe = Integer.parseInt(child_spe_Str);

                ClassInfo classInfo = ClassInfo.builder()
                        .class_0(class_0)
                        .class_1(class_1)
                        .class_2(class_2)
                        .class_3(class_3)
                        .class_4(class_4)
                        .class_5(class_5)
                        .child_0(child_0)
                        .child_1(child_1)
                        .child_2(child_2)
                        .child_3(child_3)
                        .child_4(child_4)
                        .child_5(child_5)
                        .child_spe(child_spe)
                        .build();

                // TeacherInfo Embeddable 객체 생성하고 정보 전처리 및 설정
                Integer dur_1 = Integer.parseInt(dur_1_Str);
                Integer dur12 = Integer.parseInt(dur12_Str);
                Integer dur24 = Integer.parseInt(dur24_Str);
                Integer dur46 = Integer.parseInt(dur46_Str);
                Integer dur6_ = Integer.parseInt(dur6_Str);

                TeacherInfo teacherInfo = TeacherInfo.builder()
                        .dur_1(dur_1)
                        .dur12(dur12)
                        .dur24(dur24)
                        .dur46(dur46)
                        .dur6_(dur6_)
                        .build();

                // ChildHouseInfoResponse 객체 생성하고 정보 전처리 및 설정
                Integer maxChildCnt = Integer.parseInt(maxChildCntStr);
                Integer curChildCnt = Integer.parseInt(curChildCntStr);

                ChildHouseInfoResponse childHouseInfoResponse = ChildHouseInfoResponse.builder()
                        .centerName(centerName)
                        .estType(estType)
                        .program(program)
                        .homepage(homepage)
                        .status(status)
                        .owner(owner)
                        .zipcode(zipcode)
                        .curChildCnt(curChildCnt)
                        .maxChildCnt(maxChildCnt)
                        .area(area)
                        .basicInfra(basicInfra)
                        .classInfo(classInfo)
                        .teacherInfo(teacherInfo)
                        .build();

                childHouseInfoResponseList.add(childHouseInfoResponse);
            }
        } catch (Exception e) {
            // 예외가 발생하면 스택 트레이스를 출력합니다.
            e.printStackTrace();
        }
        return childHouseInfoResponseList;
    }


    /**
     * 유치원 일반현황 조회 API 호출
     */
    private List<KindergartenGeneralResponse> getKindergartenGeneralInfo(String sidoCode, String sigunguCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenGeneralApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/basicInfo2.do";
        String url = kindergartenGeneralApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + sidoCode + "&sggCode=" + sigunguCode;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();
        List<KindergartenGeneralResponse> generalResponseList = new ArrayList<>();

        try {
            JSONObject fullResponse = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray results = (JSONArray) fullResponse.get("kinderInfo");

            for (Object result : results) {
                JSONObject temp = (JSONObject) result;

                String centerName = temp.get("kindername").toString();
                String estType = temp.get("establish").toString();
                String owner = temp.get("rppnname").toString();
                String director = temp.get("ldgrname").toString();
                String estDate = temp.get("ldgrname").toString();
                String openTime = temp.get("ldgrname").toString();
                String homepage = temp.get("ldgrname").toString();
                Integer maxChildCnt = Integer.parseInt(temp.get("prmstfcnt").toString());

                Integer class_3 = Integer.parseInt(temp.get("clcnt3").toString());
                Integer class_4 = Integer.parseInt(temp.get("clcnt4").toString());
                Integer class_5 = Integer.parseInt(temp.get("clcnt5").toString());
                Integer child_3 = Integer.parseInt(temp.get("ppcnt3").toString());
                Integer child_4 = Integer.parseInt(temp.get("ppcnt4").toString());
                Integer child_5 = Integer.parseInt(temp.get("ppcnt5").toString());
                Integer child_spe = Integer.parseInt(temp.get("shppcnt").toString());

                String[] timeParts = openTime.split("~");
                String startTime = timeParts[0].replace("시", ":").replace("분", "");
                String endTime = timeParts[1].replace("시", ":").replace("분", "");

                ClassInfo classInfo = ClassInfo.builder()
                        .class_3(class_3)
                        .class_4(class_4)
                        .class_5(class_5)
                        .child_3(child_3)
                        .child_4(child_4)
                        .child_5(child_5)
                        .child_spe(child_spe)
                        .build();

                KindergartenGeneralResponse response = KindergartenGeneralResponse.builder()
                        .centerName(centerName)
                        .estType(estType)
                        .owner(owner)
                        .director(director)
                        .estDate(estDate)
                        .startTime(startTime)
                        .endTime(endTime)
                        .homepage(homepage)
                        .maxChildCnt(maxChildCnt)
                        .classInfo(classInfo)
                        .build();

                generalResponseList.add(response);
            }

            return generalResponseList;

        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }
    }

    /**
     * 유치원 건물현황 조회 API 호출
     */
    private List<KindergartenBasicInfraResponse> getKindergartenBuildingInfo(String sidoCode, String sigunguCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenBuildingApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/building.do";
        String url = kindergartenBuildingApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + sidoCode + "&sggCode=" + sigunguCode;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();
        List<KindergartenBasicInfraResponse> buildingResponseList = new ArrayList<>();

        try {
            JSONObject fullResponse = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray results = (JSONArray) fullResponse.get("kinderInfo");

            for (Object result : results) {
                JSONObject temp = (JSONObject) result;

                String centerName = temp.get("kindername").toString();
                Integer buildingYear = Integer.parseInt(temp.get("archyy").toString());

                BasicInfra basicInfra = BasicInfra.builder()
                        .buildingYear(buildingYear)
                        .build();

                KindergartenBasicInfraResponse response = KindergartenBasicInfraResponse.builder()
                        .centerName(centerName)
                        .basicInfra(basicInfra)
                        .build();

                buildingResponseList.add(response);
            }

            return buildingResponseList;

        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }

    }

    /**
     * 유치원 교실면적현황 조회 API 호출
     */
    private List<KindergartenBasicInfraResponse> getKindergartenPhysicsInfo(String sidoCode, String sigunguCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenPhysicsApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/classArea.do";
        String url = kindergartenPhysicsApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + sidoCode + "&sggCode=" + sigunguCode;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();
        List<KindergartenBasicInfraResponse> physicsResponseList = new ArrayList<>();

        try {
            JSONObject fullResponse = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray results = (JSONArray) fullResponse.get("kinderInfo");

            for (Object result : results) {
                JSONObject temp = (JSONObject) result;

                String centerName = temp.get("kindername").toString();

                String physicsIstYnValue = temp.get("phgrindrarea").toString();
                Boolean hasPhysics = !physicsIstYnValue.equals("㎡");

                BasicInfra basicInfra = BasicInfra.builder()
                        .hasPhysics(hasPhysics)
                        .build();

                KindergartenBasicInfraResponse response = KindergartenBasicInfraResponse.builder()
                        .centerName(centerName)
                        .basicInfra(basicInfra)
                        .build();

                physicsResponseList.add(response);
            }

            return physicsResponseList;

        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }

    }

    /**
     * 유치원 통학차량현황 조회 API 호출
     */
    private List<KindergartenBasicInfraResponse> getKindergartenSchoolBusInfo(String sidoCode, String sigunguCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenSchoolBusApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/schoolBus.do";
        String url = kindergartenSchoolBusApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + sidoCode + "&sggCode=" + sigunguCode;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();
        List<KindergartenBasicInfraResponse> schoolBusResponseList = new ArrayList<>();

        try {
            JSONObject fullResponse = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray results = (JSONArray) fullResponse.get("kinderInfo");

            for (Object result : results) {
                JSONObject temp = (JSONObject) result;

                String centerName = temp.get("kindername").toString();

                String busIstYnValue = temp.get("vhcl_oprn_yn").toString();
                Boolean hasBus = "Y".equals(busIstYnValue);

                Integer busCnt = Integer.parseInt(temp.get("opra_vhcnt").toString());

                BasicInfra basicInfra = BasicInfra.builder()
                        .hasBus(hasBus)
                        .busCnt(busCnt)
                        .build();

                KindergartenBasicInfraResponse response = KindergartenBasicInfraResponse.builder()
                        .centerName(centerName)
                        .basicInfra(basicInfra)
                        .build();

                schoolBusResponseList.add(response);
            }

            return schoolBusResponseList;

        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }
    }

    /**
     * 유치원 근속연수현황 조회 API 호출
     */
    private List<KindergartenTeacherResponse> getKindergartenTeacherInfo(String sidoCode, String sigunguCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenTeacherApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/yearOfWork.do";
        String url = kindergartenTeacherApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + sidoCode + "&sggCode=" + sigunguCode;

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();
        List<KindergartenTeacherResponse> teacherResponseList = new ArrayList<>();

        try {
            JSONObject fullResponse = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray results = (JSONArray) fullResponse.get("kinderInfo");

            for (Object result : results) {
                JSONObject temp = (JSONObject) result;

                String centerName = temp.get("kindername").toString();

                Integer dur_1 = Integer.parseInt(temp.get("yy1_undr_thcnt").toString());
                Integer dur12 = Integer.parseInt(temp.get("yy1_abv_yy2_undr_thcnt").toString());
                Integer dur24 = Integer.parseInt(temp.get("yy2_abv_yy4_undr_thcnt").toString());
                Integer dur46 = Integer.parseInt(temp.get("yy4_abv_yy6_undr_thcnt").toString());
                Integer dur6_ = Integer.parseInt(temp.get("yy6_abv_thcnt").toString());

                TeacherInfo teacherInfo = TeacherInfo.builder()
                        .dur_1(dur_1)
                        .dur12(dur12)
                        .dur24(dur24)
                        .dur46(dur46)
                        .dur6_(dur6_)
                        .build();

                KindergartenTeacherResponse response = KindergartenTeacherResponse.builder()
                        .centerName(centerName)
                        .teacherInfo(teacherInfo)
                        .build();

                teacherResponseList.add(response);
            }

            return teacherResponseList;

        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }
    }

    /**
     * 유치원 안전점검ㆍ교육 실시 현황 조회 API 호출
     */

    private List<KindergartenBasicInfraResponse> getKindergartenSafetyInfo(String sidoCode, String sigunguCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        // 유치원 안전점검ㆍ교육 실시 현황 조회 API 호출
        String kindergartenSafetyApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/safetyEdu.do";
        String url = kindergartenSafetyApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + sidoCode + "&sggCode=" + sigunguCode;

        ResponseEntity<String> safetyResponseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();
        List<KindergartenBasicInfraResponse> safetyResponseList = new ArrayList<>();

        try {
            JSONObject fullResponse = (JSONObject) jsonParser.parse(safetyResponseEntity.getBody());
            JSONArray results = (JSONArray) fullResponse.get("kinderInfo");

            for (Object result : results) {
                JSONObject temp = (JSONObject) result;

                String centerName = temp.get("kindername").toString();

                String cctvIstYnValue = temp.get("cctv_ist_yn").toString();
                Boolean hasCCTV = "Y".equals(cctvIstYnValue);

                Integer cctvCnt = Integer.parseInt(temp.get("cctv_ist_total").toString());

                BasicInfra basicInfra = BasicInfra.builder()
                        .hasCCTV(hasCCTV)
                        .cctvCnt(cctvCnt)
                        .build();

                KindergartenBasicInfraResponse response = KindergartenBasicInfraResponse.builder()
                        .centerName(centerName)
                        .basicInfra(basicInfra)
                        .build();

                safetyResponseList.add(response);
            }
            return safetyResponseList;
        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }
    }
}
