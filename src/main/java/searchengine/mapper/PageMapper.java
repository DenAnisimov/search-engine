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

    public Page mapToEntity(PageDTO pageDTO) {
        return Page.builder()
                .id(pageDTO.getId())
                .path(pageDTO.getPath())
                .code(pageDTO.getCode())
                .site(siteRepository.getReferenceById(pageDTO.getSiteDTO().getId()))
                .content(pageDTO.getContent())
                .build();
    }
}
