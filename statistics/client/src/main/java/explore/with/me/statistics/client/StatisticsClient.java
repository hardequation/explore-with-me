package explore.with.me.statistics.client;

import explore.with.me.statistics.client.model.CreateEndpointHitDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsClient {

    protected final RestTemplate restTemplate;

    @Autowired
    public StatisticsClient(
            @Value("${explore-server.url}") String serverUrl,
            RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    public ResponseEntity<Object> getRecords(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris == null ? "" : String.join(",", uris),
                "unique", unique
        );

        URI uri = restTemplate.getUriTemplateHandler()
                .expand("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);

        return makeAndSendRequest(HttpMethod.GET, uri, null);
    }

    public ResponseEntity<Object> createRecord(CreateEndpointHitDto dto) {
        URI uri = restTemplate.getUriTemplateHandler().expand("/hit");
        return makeAndSendRequest(HttpMethod.POST, uri, dto);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, URI uri, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> shareitServerResponse;
        try {
            shareitServerResponse = restTemplate.exchange(uri, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
