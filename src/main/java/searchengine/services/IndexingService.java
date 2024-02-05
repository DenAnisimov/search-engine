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
import searchengine.repository.SiteRepository;
import searchengine.utils.SiteCrawl;
import searchengine.utils.StorageComponent;
import searchengine.utils.SiteProcess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class IndexingService {
    private final SiteProcess siteProcess;
    private volatile boolean isIndexingRunning = false;

    public IndexingResponse startIndexingSites() {
        synchronized (this) {
            IndexingResponse response = new IndexingResponse();
            if (isIndexingRunning) {
                response.setResult(false);
                response.setError("Индексация уже запущена");
            } else {
                siteProcess.start();
                response.setResult(true);
                isIndexingRunning = true;
            }
            return response;
        }
    }

    public IndexingResponse stopIndexing() {
        synchronized (this) {
            IndexingResponse response = new IndexingResponse();
            if (!isIndexingRunning) {
                response.setError("Индексация не запущена");
                response.setResult(false);
            } else {
                siteProcess.stop();
                isIndexingRunning = false;
                response.setResult(true);
            }
            return response;
        }
    }
}
