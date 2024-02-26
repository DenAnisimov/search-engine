package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.utils.SiteProcess;

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
