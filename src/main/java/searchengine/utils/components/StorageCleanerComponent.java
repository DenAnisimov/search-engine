package searchengine.utils.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StorageCleanerComponent {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;
    @Transactional
    public void deleteAll() {
        indexRepository.deleteAll();
        lemmaRepository.deleteAll();
        pageRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Transactional
    public void deletePage(Page page) {
        deleteAllDataLinksWithPage(page);
        pageRepository.deleteById(page.getId());
    }

    private void deleteAllDataLinksWithPage(Page page) {
        HashMap<String, Integer> lemmasFromIndex = new HashMap<>();
        List<Index> indexes = indexRepository.findAll().stream()
                .filter(i -> i.getPage().getPath().equals(page.getPath())).toList();

        for (Index index : indexes) {
            lemmasFromIndex.put(index.getLemma().getLemma(), (int) index.getLemmaRank());
        }

        List<Lemma> lemmas = lemmaRepository.findAll().stream()
                .filter(l -> l.getSite().equals(page.getSite())).toList();

        for (Map.Entry<String, Integer> lemmaFromIndex : lemmasFromIndex.entrySet()) {
            for (Lemma lemma : lemmas) {
                if (lemma.getLemma().equals(lemmaFromIndex.getKey())) {
                    lemma.setFrequency(lemma.getFrequency() - lemmaFromIndex.getValue());
                    break;
                }
            }
        }

        lemmaRepository.saveAll(lemmas);

        indexRepository.deleteAllByPage(page);
    }


}
