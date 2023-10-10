package FIS.iLUVit.domain.common.service;

import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class MapService {

    @Value("${naver.client.id}")
    private String mapClient;

    @Value("${naver.client.secret}")
    private String secretKey;

    /**
     * 주소 -> 위도, 경도
     */
    public Pair<Double, Double> convertAddressToLocation(String address) {

        WebClient webClient = WebClient.create("https://naveropenapi.apigw.ntruss.com/map-geocode/v2");

        String url = "/geocode?query=" + address;

        try {
            String responseBody = webClient.get()
                    .uri(url)
                    .header("X-NCP-APIGW-API-KEY-ID", mapClient)
                    .header("X-NCP-APIGW-API-KEY", secretKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            JsonNode addresses = responseJson.get("addresses");
            JsonNode addressResponse = addresses.get(0);

            double longitude = addressResponse.get("x").asDouble();
            double latitude = addressResponse.get("y").asDouble();

            return Pair.of(longitude, latitude);


        } catch (Exception ex){
            throw new CenterException(CenterErrorResult.ADDRESS_CONVERSION_FAILED);
        }
    }

    /**
     * 위도, 경도 -> 주소 ( 시도, 시군구 )
     */
    public Pair<String, String> getSidoSigunguByLocation(Double longitude, Double latitude){

            WebClient webClient = WebClient.builder()
                    .baseUrl("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2")
                    .defaultHeader("X-NCP-APIGW-API-KEY-ID", mapClient)
                    .defaultHeader("X-NCP-APIGW-API-KEY", secretKey)
                    .build();

            String url = "/gc?coords=" + longitude + "," + latitude + "&output=json";

            try {
                String responseBody = webClient.get()
                        .uri(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(responseBody);
                JsonNode results = responseJson.get("results");
                JsonNode temp = results.get(0);
                JsonNode region = temp.get("region");
                JsonNode area1 = region.get("area1");
                JsonNode area2 = region.get("area2");

                String sido = area1.get("name").asText();
                String sigungu = area2.get("name").asText();

                return Pair.of(sido, sigungu);
        } catch (Exception ex){
            throw new CenterException(CenterErrorResult.ADDRESS_CONVERSION_FAILED);
        }
    }

}
