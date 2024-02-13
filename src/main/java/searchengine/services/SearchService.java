package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.search.SearchResponse;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.utils.ExtractedComponent;
import searchengine.utils.LemmaFinderComponent;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmaFinderComponent lemmaFinderComponent;

    @Transactional(readOnly = true)
    public SearchResponse search(String search) {
        SearchResponse response = new SearchResponse();
        List<String> lemmas = lemmaFinderComponent.main(search).keySet().stream().toList();

        lemmas.forEach(l -> System.out.println(l));
        cleaning(lemmas);
        lemmas.forEach(l -> System.out.println(l));
        List<Page> pages = pageRepository.findAll();

        for (Page page : pages) {

        }
        return response;
    }

    private void cleaning(List<String> currentLemmas) {
        for (String lemma : currentLemmas) {
            for (Lemma lemmaDB : lemmaRepository.findAll().stream().filter(l -> l.getFrequency() > 50).toList()) {
                if (lemmaDB.getLemma().equals(lemma)) {

                    currentLemmas.remove(lemma);
                }
            }
        }
    }
}
