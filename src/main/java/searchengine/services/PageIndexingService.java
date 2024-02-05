package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.models.Index;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.utils.ExtractedComponent;
import searchengine.utils.PathUtil;
import searchengine.utils.SiteConnection;
import searchengine.utils.StorageComponent;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageIndexingService {
    private final PageRepository pageRepository;
    private final ExtractedComponent extractedComponent;
    private final SiteRepository siteRepository;
    private final SitesList sites;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final StorageComponent storageComponent;

    public IndexingResponse indexPage(String path) {
        IndexingResponse response = new IndexingResponse();
        if (path.isBlank()) {
            response.setResult(false);
            response.setError("Ссылка отсутствует");
            return response;
        }
        String url = PathUtil.getOriginalPath(path);

        if (sites.getSites().stream().noneMatch(s -> s.getUrl().equals(url))) {
            response.setResult(false);
            response.setError("Данная страница находится за пределами сайтов," +
                    "указанных в конфигурационном файле");
            return response;
        }

        if (getStatusCode(path) <= 400) {
            SiteConnection siteConnection = new SiteConnection(path);
            Page page = pageRepository.findByPath(path);
            if (page != null) {
                storageComponent.deletePage(page);
            }

                page = Page.builder()
                        .site(siteRepository.findByUrl(url))
                        .code(getStatusCode(path))
                        .content(siteConnection.getDocument().text())
                        .path(path)
                        .build();
                pageRepository.saveAndFlush(page);

                List<Lemma> lemmas = lemmaRepository.saveAllAndFlush(extractedComponent.getLemmas(page));
                indexRepository.saveAllAndFlush(extractedComponent.getIndexes(lemmas, page));
        }
        return new IndexingResponse();
    }



    private int getStatusCode(String path) {
        try {
            return Jsoup.connect(path).method(Connection.Method.GET).execute().statusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
