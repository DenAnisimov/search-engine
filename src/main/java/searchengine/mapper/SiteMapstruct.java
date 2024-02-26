package searchengine.mapper;

import org.mapstruct.Mapper;
import searchengine.dto.site.SiteDTO;
import searchengine.models.Site;

@Mapper(componentModel = "spring")
public interface SiteMapstruct {
    SiteDTO toDTO(Site site);
    Site toModel(SiteDTO siteDTO);
}
