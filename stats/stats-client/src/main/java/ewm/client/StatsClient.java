package ewm.client;

import ewm.CreateEndpointHitDto;
import ewm.EndpointStatDto;
import ewm.exception.StatsServerUnavailable;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatsClient {
    private final DiscoveryClient discoveryClient;

    private RestClient getRestClient() {
        ServiceInstance serviceInstance;

        try {
            serviceInstance = discoveryClient.getInstances("stats-server").getFirst();
        } catch (Exception ex) {
            throw new StatsServerUnavailable("Ошибка обнаружения адреса сервиса статистики с id: stats-server", ex);
        }

        String baseUrl = String.format("%s://%s:%d", serviceInstance.getScheme(), serviceInstance.getHost(), serviceInstance.getPort());
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    public ResponseEntity<Void> sendHit(CreateEndpointHitDto createEndpointHitDto) {
        return getRestClient().post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createEndpointHitDto)
                .retrieve()
                .toEntity(Void.class);
    }

    public ResponseEntity<List<EndpointStatDto>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return getRestClient().get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }
}
