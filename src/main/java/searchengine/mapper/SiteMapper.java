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

    public Site mapToEntity(SiteDTO siteDTO) {
        return Site.builder()
                .id(siteDTO.getId())
                .name(siteDTO.getName())
                .url(siteDTO.getUrl())
                .status(siteDTO.getStatus())
                .statusTime(siteDTO.getStatusTime())
                .lastError(siteDTO.getLastError())
                .build();
    }
}
