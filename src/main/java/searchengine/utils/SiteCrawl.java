package searchengine.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.dto.page.PageDTO;
import searchengine.dto.site.SiteDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SiteCrawl extends RecursiveAction {
    private static Queue<PageDTO> pageDTOQueue = new ConcurrentLinkedQueue<>();
    private static boolean crawling = true;
    private String rootLink;
    private SiteDTO siteDTO;
    public static HashSet<String> hrefs = new HashSet<>();
    private List<SiteCrawl> tasks;


    public SiteCrawl(SiteDTO siteDTO, String rootLink) {
        tasks = new ArrayList<>();
        this.siteDTO = siteDTO;

        this.rootLink = rootLink;
    }

    @Override
    protected void compute() {
        try {
            if (!crawling) {
                return;
            }
            Document document = Jsoup.connect(rootLink)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT " +
                            "5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (getStatusCode(rootLink) == 200) {

                processDocument(document);
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void processDocument(Document document) {
        if (document == null) {
            return;
        }

        Elements links = document.select("a");

        Pattern partialLink = Pattern.compile("^[\\/][a-zA-z \\/]{1,}[\\/]$|^[\\/][a-zA-z \\/]{1,}\\.html$");

        Matcher matcherPartialLink;

        for (Element link : links) {
            String href = link.attr("href");

            matcherPartialLink = partialLink.matcher(href);

            if (matcherPartialLink.find() && !hrefs.contains(href)) {

                Pattern patternOriginalLink = Pattern.compile("^[https:]{0,}[\\/]{2}[a-z.]{1,}[a-z]{2,}");
                Matcher matcherOriginalLink = patternOriginalLink.matcher(rootLink);

                if (matcherOriginalLink.find()) {
                    String originalLink = rootLink.substring(matcherOriginalLink.start(), matcherOriginalLink.end());
                    hrefs.add(href);
                    savePageData(document, originalLink + href);

                    createAndForkNewTask(originalLink + href);
                }
            }
        }
    }

    private void savePageData(Document document, String path) {
        PageDTO pageDTO = PageDTO.builder()
                .siteDTO(siteDTO)
                .code(getStatusCode(path))
                .content(document.text())
                .path(path)
                .build();
        pageDTOQueue.add(pageDTO);
    }

    private void createAndForkNewTask(String href) {
        hrefs.add(href);
        SiteCrawl task = new SiteCrawl(siteDTO, href);
        task.fork();
    }

    private int getStatusCode(String path) {
        try {
            return Jsoup.connect(path).method(Connection.Method.GET).execute().statusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Queue<PageDTO> getPageDTOQueue() {
        return pageDTOQueue;
    }

    public static void stopCrawling() {
        crawling = false;
    }
}
