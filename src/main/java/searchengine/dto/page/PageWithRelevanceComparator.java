package searchengine.dto.page;

import java.util.Comparator;

public class PageWithRelevanceComparator implements Comparator<PageWithRelevance> {

    @Override
    public int compare(PageWithRelevance o1, PageWithRelevance o2) {
        return Double.compare(o2.getRelevance(), o1.getRelevance());
    }
}
