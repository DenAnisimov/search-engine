package searchengine.utils;

import lombok.Getter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.dto.page.PageDTO;
import searchengine.dto.site.SiteDTO;

import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;

public class SiteCrawl extends RecursiveAction {
    @Getter
    private final static Queue<PageDTO> pageDTOQueue = new ConcurrentLinkedQueue<>();
    private static boolean crawling = true;
    private final String rootLink;
    private final SiteDTO siteDTO;
    public static HashSet<String> hrefs = new HashSet<>();
    private final SiteConnection siteConnection;


    public SiteCrawl(SiteDTO siteDTO, String rootLink) {
        this.siteDTO = siteDTO;

        this.rootLink = rootLink;
        siteConnection = new SiteConnection(rootLink);
    }

    @Override
    protected void compute() {
        if (!crawling) {
            return;
        }

        if (siteConnection.getStatusCode() <= 400) {
            processDocument(siteConnection.getDocument());
        }
    }

    private void processDocument(Document document) {
        if (document == null) {
            return;
        }

        savePageDTOData(document, rootLink);

        Elements links = document.select("a");

        for (Element link : links) {
            String href = link.attr("href");

            if (PathUtil.isPartialPathFind(href) && !hrefs.contains(href)) {

                if (PathUtil.isOriginalPathFind(rootLink)) {
                    String originalLink = PathUtil.getOriginalPath(rootLink);
                    hrefs.add(href);
                    createAndForkNewTask(originalLink + href);
                }
            }
        }
    }

    private void savePageDTOData(Document document, String path) {

        PageDTO pageDTO = PageDTO.builder()
                .siteDTO(siteDTO)
                .code(siteConnection.getStatusCode())
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

    public static void stopCrawling() {
        crawling = false;
    }
}
