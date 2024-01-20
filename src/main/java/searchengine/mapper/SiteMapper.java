package searchengine.mapper;

import org.springframework.stereotype.Component;
import searchengine.dto.site.SiteDTO;
import searchengine.models.Site;

@Component
public class SiteMapper {
    public Site mapToEntity(SiteDTO siteDTO) {
        Site site = new Site();
        site.setId(siteDTO.getId());
        site.setName(siteDTO.getName());
        site.setUrl(siteDTO.getUrl());
        site.setStatus(siteDTO.getStatus());
        site.setStatusTime(siteDTO.getStatusTime());
        site.setLastError(siteDTO.getLastError());
        return site;
    }

    public SiteDTO mapToDTO(Site site) {
        SiteDTO siteDTO = SiteDTO.builder()
                .id(site.getId())
                .name(site.getName())
                .url(site.getUrl())
                .status(site.getStatus())
                .statusTime(site.getStatusTime())
                .lastError(site.getLastError())
                .build();
        return siteDTO;
    }
}
