package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.site.SiteDTO;
import searchengine.mapper.SiteMapper;
import searchengine.models.Site;
import searchengine.models.enums.Status;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.utils.SiteCrawl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class IndexingService {
    private final SiteMapper siteMapper;
    private final SiteRepository siteRepository;
    private final StorageService storageService;
    private final PageRepository pageRepository;
    private final SitesList sites;
    private volatile boolean isIndexingRunning = false;

    @Async
    public IndexingResponse startIndexingSites() {
        deleteSitesAndPages();

        synchronized (this) {
            if (isIndexingRunning) {
                IndexingResponse response = new IndexingResponse();
                response.setResult(false);
                response.setError("Индексация уже запущена");
                return response;
            }
            isIndexingRunning = true;
        }


        IndexingResponse indexingResponse = new IndexingResponse();
        List<searchengine.config.Site> siteList = sites.getSites();

        if (siteList == null) {
            indexingResponse.setResult(false);
            indexingResponse.setError("Список сайтов отсутствует");
            isIndexingRunning = false;
            return indexingResponse;
        }

        List<Future<?>> futures = new ArrayList<>();

        siteList.forEach(siteConfig -> {
            Site siteDB = new Site();
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


            } catch (Exception ex) {
                siteDB.setLastError(ex.getMessage());
                siteDB.setStatus(Status.INDEXED);
                siteRepository.save(siteDB);
            }
            siteDB.setStatus(Status.INDEXED);
            siteRepository.save(siteDB);
        });

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        storageService.save(SiteCrawl.getPageDTOQueue());

        indexingResponse.setResult(true);
        isIndexingRunning = false;
        return indexingResponse;
    }

    @Async
    public IndexingResponse stopIndexing() {
        synchronized (this) {
            IndexingResponse response = new IndexingResponse();
            if (!isIndexingRunning) {
                response.setError("Индексация не запущена");
                response.setResult(false);
            } else {
                SiteCrawl.stopCrawling();
                response.setResult(true);
            }
            return response;
        }
    }

    private void deleteSitesAndPages() {
        pageRepository.deleteAll();
        siteRepository.deleteAll();
    }
}
