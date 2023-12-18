package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.Site;

public interface SiteRepository extends JpaRepository<Site, Integer> {
}
