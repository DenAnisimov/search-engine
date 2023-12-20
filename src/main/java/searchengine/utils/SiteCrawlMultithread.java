package searchengine.utils;
import searchengine.models.Site;
import searchengine.services.PageService;

import java.util.concurrent.ForkJoinPool;


public class SiteCrawlMultithread extends Thread {
    private Site site;

    public SiteCrawlMultithread(Site site) {
        this.site = site;
    }

    @Override
    public void start() {
        SiteCrawl siteCrawl = new SiteCrawl(site, site.getUrl());
        new ForkJoinPool().invoke(siteCrawl);
    }
}
