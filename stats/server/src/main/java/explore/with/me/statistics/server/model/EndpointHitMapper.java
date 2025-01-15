package explore.with.me.statistics.server.model;

import org.springframework.stereotype.Component;

@Component
public class EndpointHitMapper {

    public EndpointHit map(CreateEndpointHitDto request) {
        return EndpointHit.builder()
                .app(request.getApp())
                .uri(request.getUri())
                .ip(request.getIp())
                .timestamp(request.getTimestamp())
                .build();
    }
}
