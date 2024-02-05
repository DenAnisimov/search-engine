package searchengine.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.page.PageDTO;
import searchengine.mapper.PageMapper;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
@Component
public class StorageComponent {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;
    private final PageMapper pageMapper;
    private final ExtractedComponent extractedComponent;

    @Transactional
    public void saveAll(Queue<PageDTO> pageDTOQueue) {
        int batchSize = 100;
        int counter = 0;
        List<Index> indexes = new LinkedList<>();
        while (!pageDTOQueue.isEmpty()) {
            Page page = pageMapper.mapToEntity(pageDTOQueue.poll());
            pageRepository.save(page);

            List<Lemma> lemmas = lemmaRepository.saveAllAndFlush(extractedComponent.getLemmas(page));

            indexes.addAll(extractedComponent.getIndexes(lemmas, page));

            if (counter == batchSize || pageDTOQueue.isEmpty()) {
                pageRepository.flush();

                indexRepository.saveAllAndFlush(indexes);

                indexes.clear();
            }

            counter++;
        }
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
        indexRepository.deleteAllByPage(page);
        pageRepository.deleteById(page.getId());
    }
}
