package FIS.iLUVit.service;

import FIS.iLUVit.exception.CenterErrorResult;
import FIS.iLUVit.exception.CenterException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class MapService {

    @Value("${naver.client.id}")
    private String mapClient;

    @Value("${naver.client.secret}")
    private String secretKey;

    public Pair<Double, Double> convertAddressToLocation(String address) {
        System.out.println("mapClient = " + mapClient);
        System.out.println("mapClient = " + secretKey);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-NCP-APIGW-API-KEY-ID", mapClient);
        httpHeaders.add("X-NCP-APIGW-API-KEY", secretKey);
        String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + address;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);
        JSONParser jsonParser = new JSONParser();
        JSONObject fullResponse = null;
        try {
            fullResponse = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray jsonAddress = (JSONArray) fullResponse.get("addresses");
            JSONObject addressResponse = (JSONObject) jsonAddress.get(0);

            // x 가 longitude y가 latitude
            return Pair.of(
                    Double.parseDouble(addressResponse.get("x").toString()),
                    Double.parseDouble(addressResponse.get("y").toString())
            );
        } catch (Exception ex){
            throw new CenterException(CenterErrorResult.CENTER_WRONG_ADDRESS);
        }
    }

    public Pair<String, String> getSidoSigunguByLocation(Double longitude, Double latitude){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-NCP-APIGW-API-KEY-ID", mapClient);
        httpHeaders.add("X-NCP-APIGW-API-KEY", secretKey);
        String url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=" + longitude + ',' + latitude + "&output=json";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);
        JSONParser jsonParser = new JSONParser();
        JSONObject fullResponse = null;
        try {
            fullResponse = (JSONObject) jsonParser.parse(responseEntity.getBody());
            JSONArray results = (JSONArray) fullResponse.get("results");
            JSONObject temp = (JSONObject) results.get(0);
            JSONObject region = (JSONObject) temp.get("region");
            JSONObject area1 = (JSONObject) region.get("area1");
            JSONObject area2 = (JSONObject) region.get("area2");

            String sido = area1.get("name").toString();
            String sigungu = area2.get("name").toString();
            // x 가 longitude y가 latitude
            return Pair.of(sido, sigungu);
        } catch (Exception ex){
            throw new CenterException(CenterErrorResult.CENTER_WRONG_ADDRESS);
        }
    }

}
