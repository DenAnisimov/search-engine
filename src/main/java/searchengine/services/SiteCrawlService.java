package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.models.Page;
import searchengine.repository.PageRepository;
import searchengine.utils.SiteCrawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

@Service
@RequiredArgsConstructor
public class SiteCrawlService {
    private PageRepository pageRepository;

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    public void crawl(String url, int depth) {
        forkJoinPool.invoke(new CrawlTask(url, depth));
    }

    private class CrawlTask extends RecursiveAction {
        private final String url;
        private final int depth;

        public CrawlTask(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }

        @Override
        protected void compute() {
            if (depth > 0) {
                try {
                    Document document = Jsoup.connect(url).get();
                    processPage(document);

                    List<CrawlTask> subTasks = new ArrayList<>();

                    document.select("a[href]").stream()
                            .map(link -> link.attr("abs:href"))
                            .forEach(link -> subTasks.add(new CrawlTask(link, depth - 1)));

                    invokeAll(subTasks);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processPage(Document document) {
            Page page = new Page();
            page.setPath(document.baseUri());
            page.setContent(document.text());
            pageRepository.save(page);
        }
    }
}
