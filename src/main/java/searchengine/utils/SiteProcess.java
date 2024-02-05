package searchengine.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.site.SiteDTO;
import searchengine.mapper.SiteMapper;
import searchengine.models.enums.Status;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Component
public class SiteProcess {
    private final SiteMapper siteMapper;
    private final SiteRepository siteRepository;
    private final StorageComponent storageComponent;
    private final SitesList sites;
    @Async
    public void start() {
        List<Site> siteList = sites.getSites();

        if (siteList == null) {
            throw new NullPointerException("Список сайтов отсутствует");
        }

        storageComponent.deleteAllData();

        List<Future<?>> futures = new ArrayList<>();

        siteList.forEach(siteConfig -> {
            searchengine.models.Site siteDB = new searchengine.models.Site();
            siteDB.setName(siteConfig.getName());
            siteDB.setUrl(siteConfig.getUrl());
            siteDB.setStatusTime(LocalDateTime.now());
            siteDB.setStatus(Status.INDEXING);
            try {
                siteRepository.save(siteDB);

                SiteDTO siteDTO = siteMapper.mapToDTO(siteDB);

                ExecutorService executorService = new ForkJoinPool();
                Future<?> submit = executorService.submit(() -> {
                    SiteCrawl siteCrawl = new SiteCrawl(siteDTO, siteDTO.getUrl());
                    new ForkJoinPool().invoke(siteCrawl);
                });
                futures.add(submit);

                siteDB.setStatus(Status.INDEXED);
                siteRepository.save(siteDB);
            } catch (Exception ex) {
                siteDB.setLastError(ex.getMessage());
                siteDB.setStatus(Status.FAILED);
                siteRepository.save(siteDB);
            }
        });

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        storageComponent.saveAll(SiteCrawl.getPageDTOQueue());
    }

    public void stop() {
        SiteCrawl.stopCrawling();
    }
}
