package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.Lemma;
import searchengine.models.Site;

import java.util.List;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    List<Lemma> findAllBySite(Site site);
}
