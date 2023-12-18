package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.models.Site;
import searchengine.repository.SiteRepository;

import java.util.List;

@Service
public class SiteService {
    private final SiteRepository siteRepository;

    @Autowired
    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

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
