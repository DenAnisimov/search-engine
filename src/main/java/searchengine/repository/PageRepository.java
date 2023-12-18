package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.Page;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Integer> {
    List<Page> findAllBySiteId(int siteId);
}
