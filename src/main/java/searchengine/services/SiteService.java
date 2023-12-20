package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.models.Site;
import searchengine.models.enums.Status;
import searchengine.repository.SiteRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SiteService {
    private final SiteRepository siteRepository;

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public Site getSiteById(int id) {
        return siteRepository.getReferenceById(id);
    }

    public Site saveSite(Site site) {
        return siteRepository.save(site);
    }

    public void deleteSite(Site site) {
        siteRepository.delete(site);
    }
}
