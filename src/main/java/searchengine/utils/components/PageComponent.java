package searchengine.utils.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.page.PageDTO;
import searchengine.mapper.PageMapstruct;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.PageRepository;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PageComponent {
    private final PageMapstruct pageMapstruct;
    private final PageRepository pageRepository;
    private final BatchEntitySaver batchEntitySaver;
    private final LemmaComponent lemmaComponent;
    private final IndexComponent indexComponent;

    @Transactional
    public List<Page> save(Queue<PageDTO> pageDTOQueue) {
        List<PageDTO> pages = new ArrayList<>(pageDTOQueue);
        List<Page> pageEntities = pageMapstruct.toEntities(pages);
        return batchEntitySaver.saveInBatch(pageEntities, pageRepository);
    }

    @Transactional
    public void savePage(PageDTO pageDTO) {
        Page page = pageRepository.saveAndFlush(pageMapstruct.toEntity(pageDTO));
        saveAllDataLinksWithPage(page);
    }

    private void saveAllDataLinksWithPage(Page page) {
        List<Lemma> lemmas = lemmaComponent.saveDataWithPage(page);
        indexComponent.saveDataWithPage(page, lemmas);
    }
}
