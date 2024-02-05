package searchengine.mapper;

import org.springframework.stereotype.Component;
import searchengine.dto.site.SiteDTO;
import searchengine.models.Site;

@Component
public class SiteMapper {
    public SiteDTO mapToDTO(Site site) {
        return SiteDTO.builder()
                .id(site.getId())
                .name(site.getName())
                .url(site.getUrl())
                .status(site.getStatus())
                .statusTime(site.getStatusTime())
                .lastError(site.getLastError())
                .build();
    }
}
