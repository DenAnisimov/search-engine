package searchengine.utils.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.page.PageWithRelevance;
import searchengine.dto.page.PageWithRelevanceComparator;
import searchengine.models.Index;
import searchengine.models.Page;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PageRankerComponent {

    public List<PageWithRelevance> getPagesSortedByRelevance(List<Index> indexes) {
        List<PageWithRelevance> pagesWithRelevance = getPagesWithRelativeRelevance(indexes);

        sortPagesByRelevance(pagesWithRelevance);

        return pagesWithRelevance;
    }

    private void sortPagesByRelevance(List<PageWithRelevance> pagesWithRelevance) {
        pagesWithRelevance.sort(new PageWithRelevanceComparator());
    }

    private List<PageWithRelevance> getPagesWithRelativeRelevance(List<Index> indexes) {
        List<PageWithRelevance> pagesWithRelevance = new ArrayList<>();
        List<PageWithRelevance> pagesWithAbsoluteRelevance = getPagesWithAbsoluteRelevance(indexes);
        double maxRelevance = 0.0;

        for (PageWithRelevance pageWithAbsoluteRelevance : pagesWithAbsoluteRelevance) {
            if (maxRelevance < pageWithAbsoluteRelevance.getRelevance()) {
                maxRelevance = pageWithAbsoluteRelevance.getRelevance();
            }
        }

        for (PageWithRelevance pageWithAbsoluteRelevance : pagesWithAbsoluteRelevance) {
            double relativeRelevance = pageWithAbsoluteRelevance.getRelevance() / maxRelevance;

            PageWithRelevance pageWithRelevance = new PageWithRelevance();
            pageWithRelevance.setPage(pageWithAbsoluteRelevance.getPage());
            pageWithRelevance.setRelevance(relativeRelevance);

            pagesWithRelevance.add(pageWithRelevance);
        }

        return pagesWithRelevance;
    }

    private List<PageWithRelevance> getPagesWithAbsoluteRelevance(List<Index> indexes) {
        List<PageWithRelevance> pagesWithRelevance = new ArrayList<>();

        for (Index index : indexes) {
            int lemmaRank = (int) index.getLemmaRank();
            Page page = index.getPage();

            PageWithRelevance pageWithRelevance = new PageWithRelevance();

            if (!PageWithRelevance.containsPage(pagesWithRelevance, page)) {
                pageWithRelevance.setPage(page);
                pageWithRelevance.setRelevance((double) lemmaRank);

                pagesWithRelevance.add(pageWithRelevance);
            } else {
                int indexOfPage = PageWithRelevance.getIndex(pagesWithRelevance, page);

                pageWithRelevance.setPage(page);
                lemmaRank += pagesWithRelevance.get(indexOfPage).getRelevance();
                pageWithRelevance.setRelevance((double) lemmaRank);

                pagesWithRelevance.set(indexOfPage, pageWithRelevance);
            }
        }

        return pagesWithRelevance;
    }
}
