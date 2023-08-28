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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
    private final RestTemplate restTemplate;

    @Value("${center.api-key.child-house}")
    private String childHouseSecretKey;


    @Value("${center.api-key.kindergarten}")
    private String kindergartenSecretKey;

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
     * 어린이집 정보를 업데이트합니다
     */
    @Transactional
    public void updateChildHouseInfo() {
        List<Region> regionList = regionRepository.findAll();

        for (Region region : regionList) {
            List<ChildHouseInfoResponse> responses = updateChildHouseInfo(region.getSigunguCode());
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
     * 어린이집 기본정보 조회 API를 요청하고 응답값의 내용으로 정보 저장을 위한 시설 객체를 생성하여 반환합니다f
     */
    public List<ChildHouseInfoResponse> updateChildHouseInfo(String sigunguCode) {

        HttpHeaders httpHeaders = new HttpHeaders();

        // 어린이집 일반 정보 API URL
        String childHouseGeneralApiUrl = "http://api.childcare.go.kr/mediate/rest/cpmsapi030/cpmsapi030/request";
        // API 요청을 위한 URL 생성
        String url = childHouseGeneralApiUrl + "?key=" + childHouseSecretKey + "&arcode=" + sigunguCode + "&stcode=";
        // RestTemplate을 사용하여 API 호출 및 응답 수신
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        log.info("responseEntity : {}", responseEntity);

        // JAXB를 이용한 XML 데이터 언마샬링
        ChildHouseXmlResponseWrapper responseWrapper = unmarshalXmlResponse(responseEntity.getBody());

        List<ChildHouseXmlResponse> responseList = responseWrapper.getChildHouseInfoResponseList();

        List<ChildHouseInfoResponse> childHouseInfoResponseList = new ArrayList<>();

        // API 응답 데이터 처리
        for(ChildHouseXmlResponse response : responseList) {

            // Area 관련 정보 객체 생성
            Area area = Area.builder()
                    .sido(response.getSido())
                    .sigungu(response.getSigungu())
                    .build();

            // BasicInfra 관련 정보 전처리 및 객체 생성 ( 버스 운영 여부, 놀이터 여부, CCTV 여부 판단 )
            Boolean hasBus = "운영".equals(response.getHasBus());
            Boolean hasPlayground = response.getPlayGroundCnt() > 0;
            Boolean hasCCTV = response.getCctvCnt() > 0;

            BasicInfra basicInfra = BasicInfra.builder()
                    .hasBus(hasBus)
                    .hasPlayground(hasPlayground)
                    .hasCCTV(hasCCTV)
                    .cctvCnt(response.getCctvCnt())
                    .build();

            // ClassInfo 관련 정보 객체 생성
            ClassInfo classInfo = ClassInfo.builder()
                    .class_0(response.getClass_0())
                    .class_1(response.getClass_1())
                    .class_2(response.getClass_2())
                    .class_3(response.getClass_3())
                    .class_4(response.getClass_4())
                    .class_5(response.getClass_5())
                    .child_0(response.getChild_0())
                    .child_1(response.getChild_1())
                    .child_2(response.getChild_2())
                    .child_3(response.getChild_3())
                    .child_4(response.getChild_4())
                    .child_5(response.getChild_5())
                    .child_spe(response.getChild_spe())
                    .build();

            // Teacher 관련 정보 객체 생성
            TeacherInfo teacherInfo = TeacherInfo.builder()
                    .dur_1(response.getDur_1())
                    .dur12(response.getDur12())
                    .dur24(response.getDur24())
                    .dur46(response.getDur46())
                    .dur6_(response.getDur6_())
                    .build();

            // 어린이집 정보 응답 객체 생성
            ChildHouseInfoResponse childHouseInfoResponse = ChildHouseInfoResponse.builder()
                    .centerName(response.getCenterName())
                    .area(area)
                    .estType(response.getEstType())
                    .program(response.getProgram())
                    .homepage(response.getHomepage())
                    .status(response.getStatus())
                    .owner(response.getOwner())
                    .zipcode(response.getZipcode())
                    .curChildCnt(response.getCurChildCnt())
                    .maxChildCnt(response.getMaxChildCnt())
                    .basicInfra(basicInfra)
                    .classInfo(classInfo)
                    .teacherInfo(teacherInfo)
                    .build();

            // 어린이집 정보 응답 리스트에 추가
            childHouseInfoResponseList.add(childHouseInfoResponse);
        }

        return childHouseInfoResponseList;
    }

    /**
     * xml 포맷 데이터를 언마샬링합니다
     */
    private ChildHouseXmlResponseWrapper unmarshalXmlResponse(String xmlData) {
        try {
            // JAXB 컨텍스트 초기화
            JAXBContext jaxbContext = JAXBContext.newInstance(ChildHouseXmlResponseWrapper.class);
            // 언마샬링을 위한 언마샬러 생성
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            // 입력 문자열을 읽기 위한 StringReader 생성
            StringReader reader = new StringReader(xmlData);

            // XML 데이터를 객체로 변환하여 반환
            return (ChildHouseXmlResponseWrapper) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            // JAXB 예외 처리
            e.printStackTrace();
        }
        return null;
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
