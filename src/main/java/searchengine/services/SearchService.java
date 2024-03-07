package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.search.DetailedSearchItem;
import searchengine.dto.search.SearchResponse;
import searchengine.models.Index;
import searchengine.models.Page;
import searchengine.utils.PathUtil;
import searchengine.utils.SnippetBuilder;
import searchengine.utils.components.IndexManagerComponent;
import searchengine.utils.components.LemmaManagerComponent;
import searchengine.utils.components.PageRankerComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final LemmaManagerComponent lemmaManagerComponent;
    private final IndexManagerComponent indexManagerComponent;
    private final PageRankerComponent pageRankerComponent;

    @Transactional(readOnly = true)
    public SearchResponse search(String search, String sitesSearch, int limit, int offset) {
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

        HashMap<Page, Double> sortedPages = pageRankerComponent.getPagesSortedByRelevance(indexes);

        response.setCount(sortedPages.size());
        response.setResult(true);

        List<DetailedSearchItem> searchItems = new ArrayList<>();
        for (Map.Entry<Page, Double> page : sortedPages.entrySet()) {
                searchItems.add(createDetailedItem(page, lemmas));
        }
        response.setOffset(offset);
        response.setLimit(limit);
        response.setData(searchItems);
        return response;
    }

    private DetailedSearchItem createDetailedItem(Map.Entry<Page, Double> page, List<String> lemmas) {
        String site = page.getKey().getSite().getUrl();
        String uri = PathUtil.getPartialPath(page.getKey().getPath());
        String title = page.getKey().getSite().getName();
        Double relevance = page.getValue();

        DetailedSearchItem item = new DetailedSearchItem();
        item.setSite(site);
        item.setUri(uri);
        item.setTitle(title);
        item.setSiteName(title);
        for (String lemma : lemmas) {
            String snippet = SnippetBuilder.getSnippet(page.getKey().getContent(), lemma);
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
