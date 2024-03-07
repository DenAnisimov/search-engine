package searchengine.utils.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.utils.LemmaFinder;

import java.util.*;

@Component
@RequiredArgsConstructor
public class LemmaManagerComponent {
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    public List<String> getSortedAndFilteredLemmas(String search) {
        List<String> lemmas = new ArrayList<>(LemmaFinder.main(search).keySet());

        sortLemmasByFrequency(lemmas);
        filterLemmasByFrequency(lemmas);

        return lemmas;
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
        List<Index> indexes = indexRepository.findAll().stream().toList();
        boolean isOften = false;
        int countOfPage = 200;

        for (String lemma : lemmas) {
            int counter = 0;
            for (Index index : indexes) {
                if (index.getLemma().getLemma().equals(lemma)) {
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
