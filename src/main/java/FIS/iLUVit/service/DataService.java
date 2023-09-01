package FIS.iLUVit.service;

import FIS.iLUVit.domain.Region;
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
            updateKindergartenSchoolBusInfo(region);
            updateKindergartenPhysicsInfo(region);
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
     * 유치원 일반현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
     */
    private void updateKindergartenGeneralInfo(Region region) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenGeneralApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/basicInfo2.do";
        String url = kindergartenGeneralApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + region.getSidoCode() + "&sggCode=" + region.getSigunguCode();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();

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
                Integer class_3 = (temp.get("clcnt3") != null) ? Integer.parseInt(temp.get("clcnt3").toString()) : 0;
                Integer class_4 = (temp.get("clcnt4") != null) ? Integer.parseInt(temp.get("clcnt4").toString()) : 0;
                Integer class_5 = (temp.get("clcnt5") != null) ? Integer.parseInt(temp.get("clcnt5").toString()) : 0;
                Integer child_3 = (temp.get("ppcnt3") != null) ? Integer.parseInt(temp.get("ppcnt3").toString()) : 0;
                Integer child_4 = (temp.get("ppcnt4") != null) ? Integer.parseInt(temp.get("ppcnt4").toString()) : 0;
                Integer child_5 = (temp.get("ppcnt5") != null) ? Integer.parseInt(temp.get("ppcnt5").toString()) : 0;
                Integer child_spe = (temp.get("shppcnt") != null) ? Integer.parseInt(temp.get("shppcnt").toString()) : 0;

                // 데이터 전처리
                String startTime = null;
                String endTime = null;

                if (operTime != null) {
                    String[] timeParts = operTime.split("~");
                    startTime = timeParts[0].replace("시", ":").replace("분", "").trim();
                    endTime = timeParts[1].replace("시", ":").replace("분", "").trim();
                }

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
                        .class_3(class_3)
                        .class_4(class_4)
                        .class_5(class_5)
                        .child_3(child_3)
                        .child_4(child_4)
                        .child_5(child_5)
                        .child_spe(child_spe)
                        .build();

                centerRepository.updateKindergartenForGeneral(response, region.getSidoName(), region.getSigunguName());
            }
        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }
    }

    /**
     * 유치원 건물현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
     */
    private void updateKindergartenBuildingInfo(Region region) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenBuildingApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/building.do";
        String url = kindergartenBuildingApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + region.getSidoCode() + "&sggCode=" + region.getSigunguCode();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();

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
                centerRepository.updateCenterBuildingYear(centerName, buildingYear, region.getSidoName(), region.getSigunguName());
            }
        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }

    }

    /**
     * 유치원 교실면적현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
     */
    private void updateKindergartenPhysicsInfo(Region region) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenPhysicsApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/classArea.do";
        String url = kindergartenPhysicsApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + region.getSidoCode() + "&sggCode=" + region.getSigunguCode();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();

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
                centerRepository.updateCenterPhysics(centerName, hasPhysics, region.getSidoName(), region.getSigunguName());
            }
        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }

    }

    /**
     * 유치원 통학차량현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
     */
    private void updateKindergartenSchoolBusInfo(Region region) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenSchoolBusApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/schoolBus.do";
        String url = kindergartenSchoolBusApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + region.getSidoCode() + "&sggCode=" + region.getSigunguCode();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();

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
                centerRepository.updateCenterBus(centerName, hasBus, busCnt, region.getSidoName(), region.getSigunguName());
            }
        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }
    }

    /**
     * 유치원 근속연수현황 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
     */
    private void updateKindergartenTeacherInfo(Region region) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String kindergartenTeacherApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/yearOfWork.do";
        String url = kindergartenTeacherApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + region.getSidoCode() + "&sggCode=" + region.getSigunguCode();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();

        try {
            JSONObject fullResponse = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray results = (JSONArray) fullResponse.get("kinderInfo");

            for (Object result : results) {
                JSONObject temp = (JSONObject) result;

                // 데이터 가져오기
                String centerName = temp.get("kindername").toString();
                Integer dur_1 = (temp.get("yy1_undr_thcnt") != null) ? Integer.parseInt(temp.get("yy1_undr_thcnt").toString()) : 0;
                Integer dur12 = (temp.get("yy1_abv_yy2_undr_thcnt") != null) ? Integer.parseInt(temp.get("yy1_abv_yy2_undr_thcnt").toString()) : 0;
                Integer dur24 = (temp.get("yy2_abv_yy4_undr_thcnt") != null) ? Integer.parseInt(temp.get("yy2_abv_yy4_undr_thcnt").toString()) : 0;
                Integer dur46 = (temp.get("yy4_abv_yy6_undr_thcnt") != null) ? Integer.parseInt(temp.get("yy4_abv_yy6_undr_thcnt").toString()) : 0;
                Integer dur6_ = (temp.get("yy6_abv_thcnt") != null) ? Integer.parseInt(temp.get("yy6_abv_thcnt").toString()) : 0;

                Integer totalCnt = dur_1 + dur12 + dur24 + dur46 + dur6_;

                KindergartenTeacherResponse response = KindergartenTeacherResponse.builder()
                        .centerName(centerName)
                        .totalCnt(totalCnt)
                        .dur_1(dur_1)
                        .dur12(dur12)
                        .dur24(dur24)
                        .dur46(dur46)
                        .dur6_(dur6_)
                        .build();

                centerRepository.updateKindergartenForTeacher(response, region.getSidoName(), region.getSigunguName());
            }
        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }
    }

    /**
     * 유치원 안전점검ㆍ교육 조회 OpenAPI 호출, JSON 객체를 파싱하고 전처리하여 생성한 객체 리스트를 반환합니다
     */

    private void updateKindergartenSafetyInfo(Region region) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        // 유치원 안전점검ㆍ교육 실시 현황 조회 API 호출
        String kindergartenSafetyApiUrl = "https://e-childschoolinfo.moe.go.kr/api/notice/safetyEdu.do";
        String url = kindergartenSafetyApiUrl + "?key=" + kindergartenSecretKey + "&sidoCode=" + region.getSidoCode() + "&sggCode=" + region.getSigunguCode();

        ResponseEntity<String> safetyResponseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);

        JSONParser jsonParser = new JSONParser();

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
                centerRepository.updateCenterCCTV(centerName, hasCCTV, cctvCnt, region.getSidoName(), region.getSigunguName());
            }
        } catch (Exception ex) {
            throw new DataException(DataErrorResult.JSON_PARSE_ERROR);
        }
    }

}
