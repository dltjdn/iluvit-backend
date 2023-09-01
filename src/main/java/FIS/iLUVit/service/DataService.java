package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Region;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
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
    @Transactional
    public void updateKindergartenInfo() {
        List<Region> regionList = regionRepository.findAll();

        for (Region region : regionList) {
            updateKindergartenGeneralInfo(region);
            updateKindergartenTeacherInfo(region);
            updateKindergartenSafetyInfo(region);
            updateKindergartenBuildingInfo(region);
            updateKindergartenPhysicsInfo(region);
            updateKindergartenSchoolBusInfo(region);
        }
    }

    /**
     * 어린이집 정보를 업데이트합니다
     */
    public void updateChildHouseInfo() {
        List<Region> regionList = regionRepository.findAll();

        for (Region region : regionList) {
            getChildHouseInfo(region);
        }
    }

    /**
     * 주어진 지역 코드를 활용하여 어린이집 정보를 가져와 업데이트합니다
     */
    @Transactional
    public void getChildHouseInfo(Region region) {
        // 지역 정보에서 시군구 코드 추출
        String sigunguCode = region.getSigunguCode();
        // HTTP 요청 헤더 설정
        HttpHeaders httpHeaders = new HttpHeaders();

        // 어린이집 정보 조회 API 엔드포인트 및 파라미터 설정
        String childHouseGeneralApiUrl = "http://api.childcare.go.kr/mediate/rest/cpmsapi030/cpmsapi030/request";
        String url = childHouseGeneralApiUrl + "?key=" + childHouseSecretKey + "&arcode=" + sigunguCode + "&stcode=";

        // REST API를 통해 어린이집 정보 조회
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        // JAXB를 이용한 XML 데이터 언마샬링 ( xml 데이터를 객체로 변환 )
        ChildHouseInfoResponseWrapper responseWrapper = unmarshalXmlResponse(responseEntity.getBody());

        // 변환된 응답 객체가 유효한 경우
        if (responseWrapper != null) {
            // 어린이집 정보 리스트 추출
            List<ChildHouseInfoResponse> responseList = responseWrapper.getChildHouseInfoResponseList();
            // 어린이집 정보를 순회하며 필요한 정보를 추출하여 업데이트
            for (ChildHouseInfoResponse response : responseList) {
                // BasicInfra 데이터 전처리 ( 통학차량 운영 여부, 놀이터 여부, CCTV 여부 불리언으로 형변환 )
                Boolean hasBus = "운영".equals(response.getHasBus());
                Boolean hasPlayground = response.getPlayGroundCnt() > 0;
                Boolean hasCCTV = response.getCctvCnt() > 0;

                // TeacherInfo 데이터 전처리 ( 근속연수 정보의 소수점을 반올림하여 정수로 형변환 )
                Integer totalCnt = (response.getTotalCnt() != null) ? (int) Math.round(response.getTotalCnt()) : 0;
                Integer dur_1 = (response.getDur_1() != null) ? (int) Math.round(response.getDur_1()) : 0;
                Integer dur12 = (response.getDur12() != null) ? (int) Math.round(response.getDur12()) : 0;
                Integer dur24 = (response.getDur24() != null) ? (int) Math.round(response.getDur24()) : 0;
                Integer dur46 = (response.getDur46() != null) ? (int) Math.round(response.getDur46()) : 0;
                Integer dur6_ = (response.getDur6_() != null) ? (int) Math.round(response.getDur6_()) : 0;

                // 어린이집 정보를 업데이트하는 메서드 호출
                centerRepository.updateChildHouse(response, hasBus, hasPlayground, hasCCTV, totalCnt, dur_1, dur12, dur24, dur46, dur6_);
            }
        }
    }

    /**
     * xml 포맷 데이터를 언마샬링합니다
     */
    private ChildHouseInfoResponseWrapper unmarshalXmlResponse(String xmlData) {
        try {
            // JAXB 컨텍스트 초기화
            JAXBContext jaxbContext = JAXBContext.newInstance(ChildHouseInfoResponseWrapper.class);
            // 언마샬링을 위한 언마샬러 생성
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            // 입력 문자열을 읽기 위한 StringReader 생성
            StringReader reader = new StringReader(xmlData);

            // XML 데이터를 객체로 변환하여 반환
            return (ChildHouseInfoResponseWrapper) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            // JAXB 예외 처리
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 각 지역에 대해 유치원 정보를 가져와 중복 여부를 확인하고 지역별 유치원의 "기본정보 및 학급정보"를 업데이트합니다
     */
    private void updateKindergartenGeneralInfo(Region region) {

        List<KindergartenGeneralResponse> generalResponseList = getKindergartenGeneralInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenGeneralResponse generalResponse : generalResponseList) {
            String centerName = generalResponse.getCenterName();
            String sidoName = region.getSidoName();
            String sigunguName = region.getSigunguName();

            if (hasDuplicateCenter(centerName, sidoName, sigunguName)) {
                log.warn("시도명 {}와 시군구명 {}에 동일한 이름 {}을 가진 센터가 있습니다", centerName, sidoName, sigunguName);
                continue;  // Skip to the next iteration
            }

            Optional<Center> optionalCenter = centerRepository.findByNameAndAreaSidoAndAreaSigungu(centerName, sidoName, sigunguName);
            optionalCenter.ifPresent(center -> center.updateCenterGeneral(generalResponse));
        }
    }

    /**
     * 각 지역에 대해 유치원 정보를 가져와 중복 여부를 확인하고 지역별 유치원의 "선생님 정보"를 업데이트합니다
     */
    private void updateKindergartenTeacherInfo(Region region) {

        List<KindergartenTeacherResponse> teacherResponseList = getKindergartenTeacherInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenTeacherResponse teacherResponse : teacherResponseList) {
            String centerName = teacherResponse.getCenterName();
            String sidoName = region.getSidoName();
            String sigunguName = region.getSigunguName();

            if (hasDuplicateCenter(centerName, sidoName, sigunguName)) {
                log.warn("시도명 {}와 시군구명 {}에 동일한 이름 {}을 가진 센터가 있습니다", centerName, sidoName, sigunguName);
                continue;  // Skip to the next iteration
            }

            Optional<Center> optionalCenter = centerRepository.findByNameAndAreaSidoAndAreaSigungu(centerName, sidoName, sigunguName);
            optionalCenter.ifPresent(center -> center.updateCenterTeacher(teacherResponse));
        }
    }

    /**
     * 각 지역에 대해 유치원 정보를 가져와 중복 여부를 확인하고 지역별 유치원의 "통학차량 정보"를 업데이트합니다
     */
    private void updateKindergartenSchoolBusInfo(Region region) {

        List<KindergartenBasicInfraResponse> schoolBusResponseList = getKindergartenSchoolBusInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenBasicInfraResponse schoolResponse : schoolBusResponseList) {
            String centerName = schoolResponse.getCenterName();
            String sidoName = region.getSidoName();
            String sigunguName = region.getSigunguName();

            if (hasDuplicateCenter(centerName, sidoName, sigunguName)) {
                log.warn("시도명 {}와 시군구명 {}에 동일한 이름 {}을 가진 센터가 있습니다", centerName, sidoName, sigunguName);
                continue;  // Skip to the next iteration
            }

            Optional<Center> optionalCenter = centerRepository.findByNameAndAreaSidoAndAreaSigungu(centerName, sidoName, sigunguName);
            optionalCenter.ifPresent(center -> centerRepository.updateCenterBus(center.getName(), schoolResponse.getBasicInfra().getHasBus(), schoolResponse.getBasicInfra().getBusCnt()));
        }
    }

    /**
     * 각 지역에 대해 유치원 정보를 가져와 중복 여부를 확인하고 지역별 유치원의 "체육시설 유무 정보"를 업데이트합니다
     */
    private void updateKindergartenPhysicsInfo(Region region) {

        List<KindergartenBasicInfraResponse> physicsResponseList = getKindergartenPhysicsInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenBasicInfraResponse physicsResponse : physicsResponseList) {
            String centerName = physicsResponse.getCenterName();
            String sidoName = region.getSidoName();
            String sigunguName = region.getSigunguName();

            if (hasDuplicateCenter(centerName, sidoName, sigunguName)) {
                log.warn("시도명 {}와 시군구명 {}에 동일한 이름 {}을 가진 센터가 있습니다", centerName, sidoName, sigunguName);
                continue;  // Skip to the next iteration
            }

            Optional<Center> optionalCenter = centerRepository.findByNameAndAreaSidoAndAreaSigungu(centerName, sidoName, sigunguName);
            optionalCenter.ifPresent(center -> centerRepository.updateCenterPhysics(center.getName(), physicsResponse.getBasicInfra().getHasPhysics()));
        }
    }

    /**
     * 각 지역에 대해 유치원 정보를 가져와 중복 여부를 확인하고 지역별 유치원의 "건축년도 정보"를 업데이트합니다
     */
    private void updateKindergartenBuildingInfo(Region region) {

        List<KindergartenBasicInfraResponse> buildingResponseList = getKindergartenBuildingInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenBasicInfraResponse buildingResponse : buildingResponseList) {
            String centerName = buildingResponse.getCenterName();
            String sidoName = region.getSidoName();
            String sigunguName = region.getSigunguName();

            if (hasDuplicateCenter(buildingResponse.getCenterName(), region.getSidoName(), region.getSigunguName())) {
                log.warn("시도명 {}와 시군구명 {}에 동일한 이름 {}을 가진 센터가 있습니다", centerName, sidoName, sigunguName);
                continue;  // Skip to the next iteration
            }

            Optional<Center> optionalCenter = centerRepository.findByNameAndAreaSidoAndAreaSigungu(centerName, sidoName, sigunguName);
            optionalCenter.ifPresent(center -> centerRepository.updateCenterBuildingYear(center.getName(), buildingResponse.getBasicInfra().getBuildingYear()));
        }
    }

    /**
     * 각 지역에 대해 유치원 정보를 가져와 중복 여부를 확인하고 지역별 유치원의 "CCTV 정보"를 업데이트합니다
     */
    private void updateKindergartenSafetyInfo(Region region) {

        List<KindergartenBasicInfraResponse> safetyResponseList = getKindergartenSafetyInfo(region.getSidoCode(), region.getSigunguCode());

        for (KindergartenBasicInfraResponse safetyResponse : safetyResponseList) {
            String centerName = safetyResponse.getCenterName();
            String sidoName = region.getSidoName();
            String sigunguName = region.getSigunguName();

            if (hasDuplicateCenter(centerName, sidoName, sigunguName)) {
                log.warn("시도명 {}와 시군구명 {}에 동일한 이름 {}을 가진 센터가 있습니다", centerName, sidoName, sigunguName);
                continue;  // Skip to the next iteration
            }

            Optional<Center> optionalCenter = centerRepository.findByNameAndAreaSidoAndAreaSigungu(centerName, sidoName, sigunguName);
            optionalCenter.ifPresent(center -> centerRepository.updateCenterCCTV(center.getName(), safetyResponse.getBasicInfra().getHasCCTV(), safetyResponse.getBasicInfra().getCctvCnt()));
        }
    }

    /**
     * 유치원 일반현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
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

                // JSON 데이터 가져오기
                String centerName = temp.get("kindername").toString();
                String estType = (temp.get("establish") != null) ? temp.get("establish").toString() : null;
                String owner = (temp.get("rppnname") != null) ? temp.get("rppnname").toString() : null;
                String director = (temp.get("ldgrname") != null) ? temp.get("ldgrname").toString() : null;
                String estDate = (temp.get("odate") != null) ? temp.get("odate").toString() : null;
                String operTime = (temp.get("opertime") != null) ? temp.get("opertime").toString() : null;
                String homepage = (temp.get("hpaddr") != null) ? temp.get("hpaddr").toString() : null;
                Integer maxChildCnt = (temp.get("prmstfcnt") != null) ? Integer.parseInt(temp.get("prmstfcnt").toString()) : null;
                Integer class_3 = (temp.get("clcnt3") != null) ? Integer.parseInt(temp.get("clcnt3").toString()) : null;
                Integer class_4 = (temp.get("clcnt4") != null) ? Integer.parseInt(temp.get("clcnt4").toString()) : null;
                Integer class_5 = (temp.get("clcnt5") != null) ? Integer.parseInt(temp.get("clcnt5").toString()) : null;
                Integer child_3 = (temp.get("ppcnt3") != null) ? Integer.parseInt(temp.get("ppcnt3").toString()) : null;
                Integer child_4 = (temp.get("ppcnt4") != null) ? Integer.parseInt(temp.get("ppcnt4").toString()) : null;
                Integer child_5 = (temp.get("ppcnt5") != null) ? Integer.parseInt(temp.get("ppcnt5").toString()) : null;
                Integer child_spe = (temp.get("shppcnt") != null) ? Integer.parseInt(temp.get("shppcnt").toString()) : null;

                // 데이터 전처리
                String startTime = null;
                String endTime = null;

                if (operTime != null) {
                    String[] timeParts = operTime.split("~");
                    startTime = timeParts[0].replace("시", ":").replace("분", "").trim();
                    endTime = timeParts[1].replace("시", ":").replace("분", "").trim();
                }

                // ClassInfo 객체 생성
                ClassInfo classInfo = ClassInfo.builder()
                        .class_3(class_3)
                        .class_4(class_4)
                        .class_5(class_5)
                        .child_3(child_3)
                        .child_4(child_4)
                        .child_5(child_5)
                        .child_spe(child_spe)
                        .build();

                // 유치원 일반정보 객체 생성
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
     * 유치원 건물현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
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

                // 데이터 가져오기
                String centerName = temp.get("kindername").toString();
                String buildingYearStr = (temp.get("archyy") != null) ? temp.get("archyy").toString() : null;

                // 데이터 전처리
                Integer buildingYear = null;
                if (buildingYearStr != null) {
                    buildingYearStr = buildingYearStr.replace("년", "").trim();
                    buildingYear = Integer.parseInt(buildingYearStr);
                }

                // BasicInfra 객체 생성
                BasicInfra basicInfra = BasicInfra.builder()
                        .buildingYear(buildingYear)
                        .build();

                // 건축년도 정보 관련 객체 생성
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
     * 유치원 교실면적현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
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

                // 데이터 가져오기
                String centerName = temp.get("kindername").toString();
                String physicsArea = (temp.get("phgrindrarea") != null) ? temp.get("phgrindrarea").toString() : null;

                // 데이터 전처리
                Boolean hasPhysics = null;
                if (physicsArea != null) {
                    hasPhysics = !physicsArea.equals("㎡");
                }

                // BasicInfra 객체 생성
                BasicInfra basicInfra = BasicInfra.builder()
                        .hasPhysics(hasPhysics)
                        .build();

                // 체육장 관련 정보 객체 생성
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
     * 유치원 통학차량현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
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

                // 데이터 가져오기
                String centerName = temp.get("kindername").toString();
                String busIstYnValue = (temp.get("vhcl_oprn_yn") != null) ? temp.get("vhcl_oprn_yn").toString() : null;
                Integer busCnt = (temp.get("opra_vhcnt") != null) ? Integer.parseInt(temp.get("opra_vhcnt").toString()) : null;

                // 데이터 전처리
                Boolean hasBus = null;
                if (busIstYnValue != null) {
                    hasBus = "Y".equals(busIstYnValue);
                }

                // BasicInfra 객체 생성
                BasicInfra basicInfra = BasicInfra.builder()
                        .hasBus(hasBus)
                        .busCnt(busCnt)
                        .build();

                // 통학버스 관련 정보 객체 생성
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
     * 유치원 근속연수현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
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

                // 데이터 가져오기
                String centerName = temp.get("kindername").toString();
                Integer dur_1 = (temp.get("yy1_undr_thcnt") != null) ? Integer.parseInt(temp.get("yy1_undr_thcnt").toString()) : null;
                Integer dur12 = (temp.get("yy1_abv_yy2_undr_thcnt") != null) ? Integer.parseInt(temp.get("yy1_abv_yy2_undr_thcnt").toString()) : null;
                Integer dur24 = (temp.get("yy2_abv_yy4_undr_thcnt") != null) ? Integer.parseInt(temp.get("yy2_abv_yy4_undr_thcnt").toString()) : null;
                Integer dur46 = (temp.get("yy4_abv_yy6_undr_thcnt") != null) ? Integer.parseInt(temp.get("yy4_abv_yy6_undr_thcnt").toString()) : null;
                Integer dur6_ = (temp.get("yy6_abv_thcnt") != null) ? Integer.parseInt(temp.get("yy6_abv_thcnt").toString()) : null;

                // TeacherInfo 객체 생성
                TeacherInfo teacherInfo = TeacherInfo.builder()
                        .dur_1(dur_1)
                        .dur12(dur12)
                        .dur24(dur24)
                        .dur46(dur46)
                        .dur6_(dur6_)
                        .build();

                // 교사 정보 관련 객체 생성
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
     * 유치원 안전점검ㆍ교육 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
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

                // 데이터 가져오기
                String centerName = temp.get("kindername").toString();
                String cctvIstYnValue = (temp.get("cctv_ist_yn") != null) ? temp.get("cctv_ist_yn").toString() : null;
                Integer cctvCnt = (temp.get("cctv_ist_total") != null) ? Integer.parseInt(temp.get("cctv_ist_total").toString()) : null;

                // 데이터 전처리
                Boolean hasCCTV = null;
                if (cctvIstYnValue != null) {
                    hasCCTV = "Y".equals(cctvIstYnValue);
                }

                // BasicInfra 객체 생성
                BasicInfra basicInfra = BasicInfra.builder()
                        .hasCCTV(hasCCTV)
                        .cctvCnt(cctvCnt)
                        .build();

                // CCTV 관련 정보 객체 생성
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

    /**
     * 해당 시도, 시군구에 동일한 이름을 가진 시설이 있는지 중복을 조회합니다
     */
    private Boolean hasDuplicateCenter(String centerName, String sido, String sigungu) {
        List<Center> centerList = centerRepository.findCentersByNameAndAreaSidoAndAreaSigungu(centerName, sido, sigungu);
        return centerList.size() > 1;
    }

}
