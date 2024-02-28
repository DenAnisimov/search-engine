package searchengine.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.index.IndexDTO;
import searchengine.dto.lemma.LemmaDTO;
import searchengine.dto.page.PageDTO;
import searchengine.mapper.*;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.*;

@RequiredArgsConstructor
@Component
public class StorageComponent {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;
    private final PageMapstruct pageMapstruct;
    private final LemmaMapstruct lemmaMapstruct;
    private final IndexMapstruct indexMapstruct;
    private final ExtractedComponent extractedComponent;

    @Transactional
    public void saveAll(Queue<PageDTO> pageDTOQueue) {
        List<PageDTO> pages = new ArrayList<>(pageDTOQueue);
        List<LemmaDTO> lemmas = extractedComponent.getLemmas(pages);
        List<IndexDTO> indexes = extractedComponent.getIndexes(pages, lemmas);

        List<Page> pageEntities = pageMapstruct.toModels(pages);
        pageRepository.saveAll(pageEntities);

        List<Lemma> lemmaEntities = lemmaMapstruct.toModels(lemmas);
        lemmaRepository.saveAll(lemmaEntities);

        List<Index> indexEntities = indexMapstruct.toModels(indexes);
        indexRepository.saveAll(indexEntities);
    }

    private void addLemmaData(List<LemmaDTO> lemmas, List<LemmaDTO> lemmasToAdd) {
        for (LemmaDTO lemmaToAdd : lemmasToAdd) {
            boolean found = false;
            for (LemmaDTO lemma : lemmas) {
                if (lemma.isLemmaBelongToSite(lemmaToAdd)) {
                    lemma.setFrequency(lemma.getFrequency() + lemmaToAdd.getFrequency());
                    found = true;
                    break;
                }
            }
            if (!found) {
                lemmas.add(lemmaToAdd);
            }
        }
    }

    @Transactional
    public void savePage(PageDTO page) {
        pageRepository.save(pageMapstruct.toModel(page));
        saveAllDataLinksWithPage(page);
    }

    private void saveAllDataLinksWithPage(PageDTO page) {
        /*List<LemmaDTO> lemmasFromPage = extractedComponent.getLemmas(page);
        List<Lemma> lemmas = lemmaRepository.findAll();

        if (lemmas.isEmpty()) {
            saveInBatch(lemmaMapstruct.toModels(lemmasFromPage), lemmaRepository);
        } else {
            for (LemmaDTO lemmaFromPage : lemmasFromPage) {
                for (Lemma lemma : lemmas) {
                    if (lemmaFromPage.getLemma().equals(lemma.getLemma())) {
                        lemma.setFrequency(lemma.getFrequency() + lemmaFromPage.getFrequency());
                    }
                }
            }
            saveInBatch(lemmas, lemmaRepository);
        }

        List<IndexDTO> indexesFromPage = extractedComponent.getIndexes(lemmasFromPage, page);

        saveInBatch(indexMapstruct.toModels(indexesFromPage), indexRepository);*/
    }

    @Transactional
    public void deleteAllData() {
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
        List<Index> indexes = indexRepository.findAll().stream().filter(i -> i.getPage().getPath().equals(page.getPath())).toList();

        for (Index index : indexes) {
            lemmasFromIndex.put(index.getLemma().getLemma(), (int) index.getLemmaRank());
        }

        List<Lemma> lemmas = lemmaRepository.findAll().stream().filter(l -> l.getSite().equals(page.getSite())).toList();

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

    private <T, ID> void saveInBatch(List<T> entities, JpaRepository<T, ID> repository) {
        int batchSize = 100;
        for (int i = 0; i < entities.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, entities.size());
            List<T> sublist = entities.subList(i, endIndex);
            repository.saveAllAndFlush(sublist);
        }
    }
}
