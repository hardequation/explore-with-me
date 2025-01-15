package explore.with.me.statistics.server.service;

import explore.with.me.statistics.server.model.CreateEndpointHitDto;
import explore.with.me.statistics.server.model.EndpointHit;
import explore.with.me.statistics.server.model.EndpointHitMapper;
import explore.with.me.statistics.server.model.ViewStats;
import explore.with.me.statistics.server.repository.EndpointHitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final EndpointHitRepository hitRepository;

    private final EndpointHitMapper mapper;

    public EndpointHit saveHit(CreateEndpointHitDto hit) {
        EndpointHit toCreate = mapper.map(hit);
        return hitRepository.save(toCreate);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris != null && !uris.isEmpty()) {
            return hitRepository.findStatsByUris(start, end, uris, unique);
        }
        return hitRepository.findStats(start, end, unique);
    }
}
