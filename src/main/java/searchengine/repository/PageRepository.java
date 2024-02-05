package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.Page;
import searchengine.models.Site;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Integer> {
    Page findByPath(String path);
    List<Page> findAllBySite(Site site);
}
