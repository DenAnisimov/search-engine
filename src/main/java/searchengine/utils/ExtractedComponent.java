package searchengine.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ExtractedComponent {
    private final LemmaFinderComponent lemmaFinderComponent;
    public List<Index> getIndexes(List<Lemma> lemmas, Page page) {
        List<Index> indexes = new LinkedList<>();

        for (Lemma lemma : lemmas) {
            Index index = Index.builder()
                    .lemmaRank(lemma.getFrequency())
                    .page(page)
                    .lemma(lemma)
                    .build();

            indexes.add(index);
        }

        return indexes;
    }

    public List<Lemma> getLemmas(Page page) {
        LinkedList<Lemma> lemmas= new LinkedList<>();

        for (Map.Entry<String, Integer> pageLemmas : lemmaFinderComponent.main(page.getContent()).entrySet()) {
            Lemma lemma = Lemma.builder()
                    .site(page.getSite())
                    .lemma(pageLemmas.getKey())
                    .frequency(pageLemmas.getValue())
                    .build();

            lemmas.add(lemma);
        }

        return lemmas;
    }
}
