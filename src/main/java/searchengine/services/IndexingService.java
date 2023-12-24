package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.models.Site;
import searchengine.models.enums.Status;
import searchengine.repository.SiteRepository;
import searchengine.utils.SiteCrawlMultithread;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class IndexingService {
    private final SiteRepository siteRepository;

    private final SitesList sites;

    private IndexingResponse indexingResponse;

    // Запустить индексацию отдельно от главного потока (асинхроно) async
    public IndexingResponse startIndexingSites() {
        List<searchengine.config.Site> siteList = sites.getSites();
        indexingResponse = new IndexingResponse();

        // IF если индексация запущена, то indexingResponse.setResult(false); //
        //                indexingResponse.setError(Индексация уже запущена); //
        // return response
        // если не запущена, то запускаем и возвращаем true
        if (siteList == null) {
            indexingResponse.setResult(false);
            indexingResponse.setError("Список сайтов отсутствует");
        } else {

            for (searchengine.config.Site site : siteList) {
                Site siteDB = new Site();
                siteRepository.delete(siteDB);
                try {
                    siteDB.setName(site.getName());
                    siteDB.setUrl(site.getUrl());
                    siteDB.setStatus(Status.INDEXING);
                    siteDB.setStatusTime(LocalDateTime.now());

                    SiteCrawlMultithread siteCrawlMultithread = new SiteCrawlMultithread(siteDB);
                    siteCrawlMultithread.start();

                    siteRepository.save(siteDB);

                    indexingResponse.setResult(true);
                    return indexingResponse;
                } catch (Exception ex) {
                    siteDB.setLastError(ex.toString());
                    siteRepository.save(siteDB);

                    indexingResponse.setResult(false);
                    indexingResponse.setError(ex.toString());
                    return indexingResponse;
                }
            }
        }
        return indexingResponse;
    }

    public void stopIndexing() {

    }

    private void deleteSiteData(Site site) {
        siteRepository.deleteById(site.getId());
    }
}
