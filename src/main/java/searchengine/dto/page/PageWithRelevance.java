package searchengine.dto.page;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import searchengine.models.Page;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PageWithRelevance {
    private Page page;
    private Double relevance;

    public static boolean containsPage(List<PageWithRelevance> pageList, Page targetPage) {
        for (PageWithRelevance pageWithRelevance : pageList) {
            if (pageWithRelevance.getPage().equals(targetPage)) {
                return true;
            }
        }
        return false;
    }

    public static int getIndex(List<PageWithRelevance> pageList, Page targetPage) {
        for (PageWithRelevance pageWithRelevance : pageList) {
            if (pageWithRelevance.getPage().equals(targetPage)) {
                return pageList.indexOf(pageWithRelevance);
            }
        }
        return -1;
    }
}
