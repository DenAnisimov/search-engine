package searchengine.utils.components;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.dto.lemma.LemmaDTO;
import searchengine.mapper.LemmaMapstruct;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.LemmaRepository;

import java.util.*;

@Component
@RequiredArgsConstructor
public class LemmaComponent {
    private final LemmaFinderComponent lemmaFinderComponent;
    private final LemmaRepository lemmaRepository;
    private final BatchEntitySaver batchEntitySaver;

    @Transactional
    public List<Lemma> save(List<Page> pages) {
        return batchEntitySaver.saveInBatch(getLemmas(pages), lemmaRepository);
    }

    private List<Lemma> getLemmas(List<Page> pages) {
        List<Lemma> lemmas = new LinkedList<>();

        for (Page page : pages) {
            Map<String, Integer> pageLemmaMap = lemmaFinderComponent.main(page.getContent());

            for (Map.Entry<String, Integer> entry : pageLemmaMap.entrySet()) {
                String lemmaText = entry.getKey();
                Integer frequency = entry.getValue();

                boolean lemmaExists = false;
                for (Lemma lemma : lemmas) {
                    if (lemma.getLemma().equals(lemmaText)) {
                        lemma.setFrequency(lemma.getFrequency() + frequency);
                        lemmaExists = true;
                        break;
                    }
                }

                if (!lemmaExists) {
                    Lemma lemmaEntity = Lemma.builder()
                            .site(page.getSite())
                            .lemma(lemmaText)
                            .frequency(frequency)
                            .build();
                    lemmas.add(lemmaEntity);
                }
            }
        }

        return lemmas;
    }

    @Transactional
    public List<Lemma> saveDataWithPage(Page page) {
        List<Page> pages = new ArrayList<>();
        pages.add(page);
        List<Lemma> lemmasFromPage = getLemmas(pages);
        List<Lemma> lemmas = lemmaRepository.findAll();

        if (!lemmas.isEmpty()) {
            for (Lemma lemmaFromPage : lemmasFromPage) {
                for (Lemma lemma : lemmas) {
                    if (lemmaFromPage.getLemma().equals(lemma.getLemma())) {
                        lemma.setFrequency(lemma.getFrequency() + lemmaFromPage.getFrequency());
                    }
                }
            }
        }
        return batchEntitySaver.saveInBatch(lemmas, lemmaRepository);
    }
}
