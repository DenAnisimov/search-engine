package searchengine.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.dto.site.SiteDTO;
import searchengine.mapper.SiteMapstruct;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.models.Site;
import searchengine.utils.components.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Component
public class SiteProcess {
    private final SiteMapstruct siteMapstruct;
    private final PageComponent pageComponent;
    private final SiteComponent siteComponent;
    private final LemmaComponent lemmaComponent;
    private final IndexComponent indexComponent;
    private final StorageCleanerComponent storageCleanerComponent;

    @Async
    public void start() {
        storageCleanerComponent.deleteAll();

        List<Future<?>> futures = new ArrayList<>();

        for (Site site : siteComponent.createAndSaveSites()) {
            try {

                SiteDTO siteDTO = siteMapstruct.toDTO(site);

                ExecutorService executorService = new ForkJoinPool();
                Future<?> submit = executorService.submit(() -> {
                    SiteCrawl siteCrawl = new SiteCrawl(siteDTO, siteDTO.getUrl());
                    new ForkJoinPool().invoke(siteCrawl);
                });
                futures.add(submit);
            } catch (Exception ex) {
                siteComponent.updateSite(site, ex.getMessage());
            }
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        List<Page> pages = pageComponent.save(SiteCrawl.getPageDTOQueue());
        List<Lemma> lemmas = lemmaComponent.save(pages);
        indexComponent.save(lemmas, pages);
    }


    public void stop() {
        SiteCrawl.stopCrawling();
    }
}
