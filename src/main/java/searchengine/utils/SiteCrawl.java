package Services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SiteCrawl {
    public class DataCollector extends RecursiveAction {
        private String rootLink;

        private static TreeMap<String, Integer> allLinks = new TreeMap<>();
        private static HashSet<String> hrefs = new HashSet<>();
        private List<DataCollector> tasks;

        public DataCollector(String rootLink) {
            tasks = new ArrayList<>();

            this.rootLink = rootLink;

            int depth = calculateDepth(rootLink, '/', 3);
            allLinks.put(rootLink, depth);
        }

        private Document dataToDocument(String path) {
            try {
                Document document = Jsoup.connect(path).get();
                return document;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void compute() {
            Document document = dataToDocument(rootLink);
            Elements links = document.select("a");

            Pattern partialLink = Pattern.compile("^[\\/][a-zA-z \\/]{1,}[\\/]$");

            Matcher matcherPartialLink;

            for (Element link : links) {
                String href = link.attr("href");

                matcherPartialLink = partialLink.matcher(href);

                if (matcherPartialLink.find() && !hrefs.contains(href)) {

                    Pattern patternOriginalLink = Pattern.compile("^[https:]{0,}[\\/]{2}[a-z]{1,}[.][a-z]{2,}");
                    Matcher matcherOriginalLink = patternOriginalLink.matcher(rootLink);

                    if (matcherOriginalLink.find()) {
                        String originalLink = rootLink.substring(matcherOriginalLink.start(), matcherOriginalLink.end());
                        hrefs.add(href);

                        DataCollector task = new DataCollector(originalLink + href);
                        tasks.add(task);
                        task.fork();
                        task.join();
                    }
                }
            }
        }

        public int calculateDepth(String link, char character, int requiredCharacters) {
            int allCharacters = (int) (link.chars().filter(ch -> ch == character).count());
            return allCharacters - requiredCharacters;
        }

        public void writeAllLinksToFile() {
            try (FileWriter writer = new FileWriter("sitemap.txt", true)) {
                for (Map.Entry<String, Integer> link : allLinks.entrySet()) {
                    for (int i = 0; i < link.getValue(); i++) {
                        writer.write("\t");
                    }
                    writer.write(link.getKey() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
