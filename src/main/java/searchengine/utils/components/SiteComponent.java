package searchengine.utils.components;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.models.Site;
import searchengine.config.SitesList;
import searchengine.models.enums.Status;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SiteComponent {
    private final SiteRepository siteRepository;
    private final SitesList sitesList;

    @Transactional
    public List<Site> createAndSaveSites() {
        List<searchengine.config.Site> siteList = sitesList.getSites();
        List<Site> sites = new ArrayList<>();

        siteList.forEach(siteConfig -> {
            Site site = new Site();
            site.setName(siteConfig.getName());
            site.setUrl(siteConfig.getUrl());
            site.setStatusTime(LocalDateTime.now());
            site.setStatus(Status.INDEXING);
            sites.add(site);
        });

        siteRepository.saveAll(sites);
        return sites;
    }

    @Transactional
    public void updateSite(Site site, String lastError) {
        site.setLastError(lastError);
        site.setStatus(Status.FAILED);
        siteRepository.save(site);
    }
}
