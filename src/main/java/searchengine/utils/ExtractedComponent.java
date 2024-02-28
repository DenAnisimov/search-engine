package searchengine.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.index.IndexDTO;
import searchengine.dto.lemma.LemmaDTO;
import searchengine.dto.page.PageDTO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class ExtractedComponent {
    private final LemmaFinderComponent lemmaFinderComponent;

    public List<LemmaDTO> getLemmas(List<PageDTO> pages) {
        List<LemmaDTO> lemmas = new LinkedList<>();
        HashMap<String, Integer> lemmaFrequencyMap = new HashMap<>();
        for (PageDTO page : pages) {
            Map<String, Integer> pageLemmaMap = lemmaFinderComponent.main(page.getContent());

            for (Map.Entry<String, Integer> entry : pageLemmaMap.entrySet()) {
                String lemma = entry.getKey();
                int frequency = entry.getValue();

                if (lemmaFrequencyMap.containsKey(lemma)) {
                    int newFrequency = lemmaFrequencyMap.get(lemma) + frequency;
                    lemmaFrequencyMap.put(lemma, newFrequency);
                } else {
                    lemmaFrequencyMap.put(lemma, frequency);
                    LemmaDTO lemmaDTO = LemmaDTO.builder()
                            .siteDTO(page.getSiteDTO())
                            .lemma(lemma)
                            .frequency(frequency)
                            .build();
                    lemmas.add(lemmaDTO);
                }
            }
        }

        return lemmas;
    }

    public List<IndexDTO> getIndexes(List<PageDTO> pages, List<LemmaDTO> lemmas) {
        List<IndexDTO> indexes = new LinkedList<>();
        for (PageDTO page : pages) {
            Map<String, Integer> pageLemmaMap = lemmaFinderComponent.main(page.getContent());

            for (Map.Entry<String, Integer> entry : pageLemmaMap.entrySet()) {
                String lemma = entry.getKey();
                int frequency = entry.getValue();
                LemmaDTO lemmaDTO = findLemmaDTOByLemma(lemmas, lemma);

                if (lemmaDTO != null) {
                    IndexDTO indexDTO = IndexDTO.builder()
                            .lemmaDTO(lemmaDTO)
                            .pageDTO(page)
                            .lemmaRank(frequency)
                            .build();

                    indexes.add(indexDTO);
                }
            }
        }
        return indexes;
    }

    private LemmaDTO findLemmaDTOByLemma(List<LemmaDTO> lemmas, String lemma) {
        for (LemmaDTO lemmaDTO : lemmas) {
            if (lemmaDTO.getLemma().equals(lemma)) {
                return lemmaDTO;
            }
        }
        return null;
    }
}
