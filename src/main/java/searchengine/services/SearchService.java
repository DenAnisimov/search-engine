package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.page.PageWithRelevance;
import searchengine.dto.search.DetailedSearchItem;
import searchengine.dto.search.SearchResponse;
import searchengine.models.Index;
import searchengine.models.Page;
import searchengine.utils.PathUtil;
import searchengine.utils.SnippetBuilder;
import searchengine.utils.components.IndexManagerComponent;
import searchengine.utils.components.LemmaManagerComponent;
import searchengine.utils.components.PageRankerComponent;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final LemmaManagerComponent lemmaManagerComponent;
    private final IndexManagerComponent indexManagerComponent;
    private final PageRankerComponent pageRankerComponent;

    @Transactional(readOnly = true)
    public SearchResponse search(String search, String sitesSearch, int limit, int offset) {
        log.info("Offset - {}", offset);
        SearchResponse response = new SearchResponse();

        if (search.isBlank()) {
            response.setResult(false);
            response.setError("Данной информации нет на страницах");
        }

        List<String> lemmas = lemmaManagerComponent.getSortedAndFilteredLemmas(search);

        List<Index> indexes = indexManagerComponent.getIndexesByLemmaAndSite(lemmas, sitesSearch);

        if (indexes.isEmpty()) {
            response.setResult(false);
            response.setError("Данной информации нет на страницах");
        }

        List<PageWithRelevance> sortedPages = pageRankerComponent.getPagesSortedByRelevance(indexes);
        response.setCount(sortedPages.size());
        response.setResult(true);

        List<DetailedSearchItem> searchItems = new ArrayList<>();

        int endIndex = Math.min(sortedPages.size(), limit + offset);
        for (int i = offset; i < endIndex; i++) {
            searchItems.add(createDetailedItem(sortedPages.get(i), lemmas));
        }

        response.setData(searchItems);
        return response;
    }

    private DetailedSearchItem createDetailedItem(PageWithRelevance page, List<String> lemmas) {
        String site = page.getPage().getSite().getUrl();
        String uri = PathUtil.getPartialPath(page.getPage().getPath());
        String title = page.getPage().getSite().getName();
        Double relevance = page.getRelevance();

        DetailedSearchItem item = new DetailedSearchItem();
        item.setSite(site);
        item.setUri(uri);
        item.setTitle(title);
        item.setSiteName(title);

        for (String lemma : lemmas) {
            String snippet = SnippetBuilder.getSnippet(page.getPage().getContent(), lemma);
            if (item.getSnippet() == null) {
                item.setSnippet(snippet);
            } else {
                item.setSite(item.getSnippet() + " " + snippet);
            }
        }

        item.setRelevance(relevance);
        return item;
    }
}
