package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.models.Site;
import searchengine.models.enums.Status;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.utils.SiteCrawlMultithread;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SiteIndexingService {
    private final SiteRepository siteRepository;

    private final PageRepository pageRepository;

    private final SitesList sites;

    public void indexSite() {
        List<searchengine.config.Site> siteList = sites.getSites();

        for (searchengine.config.Site site : siteList) {
            Site siteDB = new Site();

            try {
                siteDB.setName(site.getName());
                siteDB.setUrl(site.getUrl());
                siteDB.setStatus(Status.INDEXING);
                siteDB.setStatusTime(LocalDateTime.now());

                crawlAndIndexPages(siteDB);

                siteRepository.save(siteDB);
            } catch (Exception ex) {
                siteDB.setLastError(ex.toString());
                siteRepository.save(siteDB);
            }
        }
    }

    private void deleteSiteData(Site site) {
        siteRepository.deleteById(site.getId());
    }

    private void crawlAndIndexPages(Site site) {
        SiteCrawlMultithread siteCrawlMultithread = new SiteCrawlMultithread(site);
        siteCrawlMultithread.start();
    }
}
