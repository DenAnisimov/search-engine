package searchengine.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.index.IndexDTO;
import searchengine.models.Index;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IndexMapper {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteMapper siteMapper;
    public Index mapToEntity(IndexDTO indexDTO) {
        return Index.builder()
                .id(indexDTO.getId())
                .page(pageRepository.findByPath(indexDTO.getPageDTO().getPath()))
                .lemma(lemmaRepository.findByLemmaAndSite(indexDTO.getLemmaDTO().getLemma(), siteMapper.mapToEntity(indexDTO.getLemmaDTO().getSiteDTO())))
                .lemmaRank(indexDTO.getLemmaRank())
                .build();
    }

    public List<Index> mapToEntities(List<IndexDTO> indexDTOS) {
        return indexDTOS.stream().map(this::mapToEntity).toList();
    }
}
