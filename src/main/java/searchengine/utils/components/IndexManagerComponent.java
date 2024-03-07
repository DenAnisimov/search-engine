package searchengine.utils.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.models.Index;
import searchengine.repository.IndexRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IndexManagerComponent {
    private final IndexRepository indexRepository;

    public List<Index> getIndexesByLemmaAndSite(List<String> lemmas, String site) {
        List<Index> indexesByLemma = new ArrayList<>();
        List<Index> indexes = indexRepository.findAll().stream().toList();
        for (String lemma : lemmas) {
            for (Index index : indexes) {
                if (site == null) {
                    if (index.getLemma().getLemma().equals(lemma)) {
                        indexesByLemma.add(index);
                    }
                } else {
                    if (index.getLemma().getLemma().equals(lemma) && index.getLemma().getSite().getUrl().equals(site)) {
                        indexesByLemma.add(index);
                    }
                }
            }
        }

        return indexesByLemma;
    }
}
