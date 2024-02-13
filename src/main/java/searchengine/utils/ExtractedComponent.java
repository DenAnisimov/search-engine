package searchengine.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.index.IndexDTO;
import searchengine.dto.lemma.LemmaDTO;
import searchengine.dto.page.PageDTO;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class ExtractedComponent {
    private final LemmaFinderComponent lemmaFinderComponent;
    public List<IndexDTO> getIndexes(List<LemmaDTO> lemmas, PageDTO page) {
        List<IndexDTO> indexes = new LinkedList<>();

        for (LemmaDTO lemmaToAdd : lemmas) {
                    IndexDTO index = IndexDTO.builder()
                            .lemmaRank(lemmaToAdd.getFrequency())
                            .pageDTO(page)
                            .lemmaDTO(lemmaToAdd)
                            .build();

                    indexes.add(index);
            }

        return indexes;
    }

    public List<LemmaDTO> getLemmas(PageDTO page) {
        LinkedList<LemmaDTO> lemmas = new LinkedList<>();

        for (Map.Entry<String, Integer> pageLemmas : lemmaFinderComponent.main(page.getContent()).entrySet()) {
            LemmaDTO lemma = LemmaDTO.builder()
                    .siteDTO(page.getSiteDTO())
                    .lemma(pageLemmas.getKey())
                    .frequency(pageLemmas.getValue())
                    .build();

            lemmas.add(lemma);
        }

        return lemmas;
    }
}
