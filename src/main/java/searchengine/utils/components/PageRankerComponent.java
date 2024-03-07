package searchengine.utils.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.models.Index;
import searchengine.models.Page;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PageRankerComponent {

    public HashMap<Page, Double> getPagesSortedByRelevance(List<Index> indexes) {
        HashMap<Page, Double> pagesWithRelevance = getPagesWithRelativeRelevance(indexes);

        return sortPagesByRelevance(pagesWithRelevance);
    }

    private HashMap<Page, Double> sortPagesByRelevance(HashMap<Page, Double> pagesWithRelevance) {

        List<Map.Entry<Page, Double>> list = new LinkedList<>(pagesWithRelevance.entrySet());

        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        HashMap<Page, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Page, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private HashMap<Page, Double> getPagesWithRelativeRelevance(List<Index> indexes) {
        HashMap<Page, Double> pagesWithRelevance = new HashMap<>();
        HashMap<Page, Integer> pagesWithAbsoluteRelevance = getPagesWithAbsoluteRelevance(indexes);
        double maxRelevance = 0.0;

        for (Map.Entry<Page, Integer> pageWithAbsoluteRelevance : pagesWithAbsoluteRelevance.entrySet()) {
            if (maxRelevance < pageWithAbsoluteRelevance.getValue()) {
                maxRelevance = pageWithAbsoluteRelevance.getValue();
            }
        }

        for (Map.Entry<Page, Integer> pageWithAbsoluteRelevance : pagesWithAbsoluteRelevance.entrySet()) {
            double relativeRelevance = pageWithAbsoluteRelevance.getValue() / maxRelevance;
            pagesWithRelevance.put(pageWithAbsoluteRelevance.getKey(), relativeRelevance);
        }

        return pagesWithRelevance;
    }

    private HashMap<Page, Integer> getPagesWithAbsoluteRelevance(List<Index> indexes) {
        HashMap<Page, Integer> pagesWithRelevance = new HashMap<>();

        for (Index index : indexes) {
            int lemmaRank = (int) index.getLemmaRank();
            Page page = index.getPage();

            if (!pagesWithRelevance.containsKey(index)) {
                pagesWithRelevance.put(page, lemmaRank);
            } else {
                lemmaRank += pagesWithRelevance.get(index);
                pagesWithRelevance.put(page, lemmaRank);
            }
        }

        return pagesWithRelevance;
    }
}
