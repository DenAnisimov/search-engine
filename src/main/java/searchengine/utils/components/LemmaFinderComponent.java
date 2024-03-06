package searchengine.utils.components;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class LemmaFinderComponent {
    private static final String WORD_REGEX = "[а-яА-Я]+";
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    public HashMap<String, Integer> main(String text) {
        List<String> words = splitTextIntoWords(text);
        HashMap<String, Integer> lemmas = new HashMap<>();
        try {
            LuceneMorphology luceneMorph = new RussianLuceneMorphology();

            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }

                List<String> wordBaseForm = luceneMorph.getMorphInfo(word);
                if (anyWordContainsParticle(wordBaseForm)) {
                    continue;
                }

                List<String> wordNormalForm = luceneMorph.getNormalForms(word);
                if (wordNormalForm.isEmpty()) {
                    continue;
                }

                String normalWord = wordNormalForm.get(0);
                if (lemmas.containsKey(normalWord)) {
                    lemmas.put(normalWord, lemmas.get(normalWord) + 1);
                } else {
                    lemmas.put(normalWord, 1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lemmas;
    }

    private boolean anyWordContainsParticle(List<String> wordBaseForm) {
        return wordBaseForm.stream().anyMatch(this::hasParticleProperty);
    }

    private boolean hasParticleProperty(String wordBase) {
        return Arrays.stream(particlesNames).anyMatch(wordBase::contains);
    }

    private List<String> splitTextIntoWords(String text) {
        text = text.trim();
        List<String> words = new ArrayList<>();
        Pattern pattern = Pattern.compile(WORD_REGEX);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            words.add(matcher.group().toLowerCase());
        }
        return words;
    }
}
