package searchengine.utils.components;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.IndexRepository;
import searchengine.utils.LemmaFinder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IndexComponent {
    private final IndexRepository indexRepository;
    private final BatchEntitySaver batchEntitySaver;

    @Transactional
    public List<Index> save(List<Lemma> lemmas, List<Page> pages) {
        return batchEntitySaver.saveInBatch(getIndexes(pages, lemmas), indexRepository);
    }

    private List<Index> getIndexes(List<Page> pages, List<Lemma> lemmas) {
        List<Index> indexes = new LinkedList<>();
        for (Page page : pages) {
            Map<String, Integer> pageLemmaMap = LemmaFinder.main(page.getContent());

            for (Map.Entry<String, Integer> entry : pageLemmaMap.entrySet()) {
                String lemma = entry.getKey();
                int frequency = entry.getValue();
                Lemma lemmaEntity = findLemmaByLemma(lemmas, lemma);

                if (lemmaEntity != null) {
                    Index indexDTO = Index.builder()
                            .lemma(lemmaEntity)
                            .page(page)
                            .lemmaRank(frequency)
                            .build();

                    indexes.add(indexDTO);
                }
            }
        }
        return indexes;
    }

    private Lemma findLemmaByLemma(List<Lemma> lemmas, String lemma) {
        for (Lemma lemmaEntity : lemmas) {
            if (lemmaEntity.getLemma().equals(lemma)) {
                return lemmaEntity;
            }
        }
        return null;
    }

    @Transactional
    public void saveDataWithPage(Page page, List<Lemma> lemmas) {
        List<Page> pages = new ArrayList<>();
        pages.add(page);
        List<Index> indexesFromPage = getIndexes(pages, lemmas);

        batchEntitySaver.saveInBatch(indexesFromPage, indexRepository);
    }
}
