package searchengine.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.page.PageDTO;
import searchengine.models.Page;
import searchengine.repository.SiteRepository;

@Component
@RequiredArgsConstructor
public class PageMapper {
    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;

    public Page mapToEntity(PageDTO pageDTO) {
        Page page = Page.builder()
                .path(pageDTO.getPath())
                .code(pageDTO.getCode())
                .site(siteRepository.getReferenceById(pageDTO.getSiteDTO().getId()))
                .content(pageDTO.getContent())
                .build();
        return page;
    }

    public PageDTO mapToDTO(Page page) {
        PageDTO pageDTO = PageDTO.builder()
                .path(page.getPath())
                .code(page.getCode())
                .siteDTO(siteMapper.mapToDTO(page.getSite()))
                .content(page.getContent())
                .build();
        return pageDTO;
    }
}
