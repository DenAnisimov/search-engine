package searchengine.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.models.Page;
import searchengine.models.Site;
import searchengine.repository.PageRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SiteCrawl extends RecursiveAction {
    @Autowired
    private PageRepository pageRepository;
    private String rootLink;
    private Site site;
    private static HashSet<String> hrefs = new HashSet<>();
    private List<SiteCrawl> tasks;

    public SiteCrawl(Site site, String rootLink) {
        tasks = new ArrayList<>();
        this.site = site;

        this.rootLink = rootLink;
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

        Page page = new Page();
        page.setSite(site);
        page.setCode(getStatusCode(rootLink));
        page.setContent(document.text());
        page.setPath(rootLink);
        pageRepository.save(page);

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

                    SiteCrawl task = new SiteCrawl(site,originalLink + href);
                    tasks.add(task);
                    task.fork();
                    task.join();
                }
            }
        }
    }

    private int getStatusCode(String path){
        try {
            return Jsoup.connect(path)
                    .method(Connection.Method.GET)
                    .execute().statusCode();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
