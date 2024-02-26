package searchengine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import searchengine.dto.page.PageDTO;
import searchengine.models.Page;

@Mapper(componentModel = "spring", uses = SiteMapstruct.class)
public interface PageMapstruct {
    @Mapping(target = "siteDTO", source = "site")
    PageDTO toDTO(Page page);
    @Mapping(target = "site", source = "siteDTO")
    Page toModel(PageDTO pageDTO);
}
