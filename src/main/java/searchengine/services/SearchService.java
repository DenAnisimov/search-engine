package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.search.DetailedSearchItem;
import searchengine.dto.search.SearchResponse;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.utils.components.LemmaFinderComponent;
import searchengine.utils.PathUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmaFinderComponent lemmaFinderComponent;

    @Transactional(readOnly = true)
    public SearchResponse search(String search, String sitesSearch, int limit, int offset) {
        SearchResponse response = new SearchResponse();

        if (search.isBlank()) {
            response.setResult(false);
            response.setError("Данной информации нет на страницах");
        }

        ArrayList<String> lemmas = new ArrayList<>(lemmaFinderComponent.main(search).keySet());

        filterLemmasByFrequency(lemmas);
        sortLemmasByFrequency(lemmas);

        List<Index> indexes = getIndexesByLemmaAndSite(lemmas, sitesSearch);

        if (indexes.isEmpty()) {
            response.setResult(false);
            response.setError("Данной информации нет на страницах");
        }

        HashMap<Page, Double> pagesWithRelevance = getPagesWithRelativeRelevance(indexes);

        HashMap<Page, Double> sortedPages = sortPagesByRelevance(pagesWithRelevance);
        sortedPages.forEach((p,d) -> System.out.println(p.getPath()));

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
            String snippet = getSnippet(page.getKey().getContent(), lemma);
            if (item.getSnippet() == null) {
                item.setSnippet(snippet);
            } else {
                item.setSite(item.getSnippet() + " " + snippet);
            }
        }
        item.setRelevance(relevance);
        return item;
    }

    private String getSnippet(String content, String searchTerm) {
        final int CONTEXT_LENGTH = 3;

        StringBuilder result = new StringBuilder();

        String[] sentences = content.split("[.!?]");

        if (searchTerm.length() >= 5) {
            searchTerm = searchTerm.substring(0, searchTerm.length() - 2);
        }

        Pattern pattern = Pattern.compile("\\b(" + searchTerm + "[а-яА-Я]*)\\b", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher;

        for (String sentence : sentences) {
            matcher = pattern.matcher(sentence);
            if (matcher.find()) {
                String[] words = sentence.split("\\s+");

                int index = -1;
                for (int i = 0; i < words.length; i++) {
                    if (words[i].contains(searchTerm)) {
                        index = i;
                        break;
                    }
                }

                int start = Math.max(0, index - CONTEXT_LENGTH);
                int end = Math.min(words.length, index + CONTEXT_LENGTH + 1);
                if (start > 0) {
                    result.append("... ");
                }
                for (int i = start; i < end; i++) {
                    if (i == index) {
                        result.append("<b>").append(words[i]).append("</b>").append(" ");
                    } else {
                        result.append(words[i]).append(" ");
                    }
                }
                if (end < words.length) {
                    result.append("...");
                }
                result.append(" ");

                matcher.reset();
            }
        }

        return result.toString().trim();
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

    private List<Index> getIndexesByLemmaAndSite(List<String> lemmas, String site) {
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

    private void sortLemmasByFrequency(List<String> lemmas) {
        HashMap<String, Integer> lemmaFrequencyHashMap = new HashMap<>();
        List<Lemma> lemmasDB = lemmaRepository.findAll().stream().toList();

        for (String lemma : lemmas) {
            for (Lemma lemmaDB : lemmasDB) {
                if (lemmaDB.getLemma().equals(lemma)) {
                    lemmaFrequencyHashMap.put(lemma, lemmaDB.getFrequency());
                    break;
                }
            }
        }

        List<String> sortedLemmas = lemmaFrequencyHashMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .toList();

        lemmas.clear();

        lemmas.addAll(sortedLemmas);
    }

    private void filterLemmasByFrequency(List<String> lemmas) {
        List<String> cleanLemmas = new ArrayList<>();
        List<Index> indexesDB = indexRepository.findAll().stream().toList();
        boolean isOften = false;
        int countOfPage = 200;

        for (String lemma : lemmas) {
            int counter = 0;
            for (Index indexDB : indexesDB) {
                if (indexDB.getLemma().getLemma().equals(lemma)) {
                    counter++;
                }
                if (counter > countOfPage) {
                    isOften = true;
                    break;
                }
            }

            if (!isOften) {
                cleanLemmas.add(lemma);
            }

            isOften = false;
        }

        lemmas.clear();

        lemmas.addAll(cleanLemmas);
    }
}
