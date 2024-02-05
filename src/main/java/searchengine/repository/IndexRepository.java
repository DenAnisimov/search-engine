package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.Index;
import searchengine.models.Page;

public interface IndexRepository extends JpaRepository<Index, Integer> {
    void deleteAllByPage(Page page);
}
