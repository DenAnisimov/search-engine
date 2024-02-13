package searchengine.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.lemma.LemmaDTO;
import searchengine.models.Lemma;
import searchengine.repository.SiteRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LemmaMapper {
    private final SiteRepository siteRepository;
    public Lemma mapToEntity(LemmaDTO lemmaDTO) {
        return Lemma.builder()
                .site(siteRepository.getReferenceById(lemmaDTO.getSiteDTO().getId()))
                .frequency(lemmaDTO.getFrequency())
                .id(lemmaDTO.getId())
                .lemma(lemmaDTO.getLemma())
                .build();
    }

    public List<Lemma> mapToEntities(List<LemmaDTO> lemmaDTOS) {
        return lemmaDTOS.stream().map(this::mapToEntity).toList();
    }
}
