package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.page.PageDTO;
import searchengine.mapper.SiteMapper;
import searchengine.models.Page;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.utils.PathUtil;
import searchengine.utils.SiteConnection;
import searchengine.utils.StorageComponent;

@Service
@RequiredArgsConstructor
public class PageIndexingService {
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;
    private final StorageComponent storageComponent;

    public IndexingResponse indexPage(String path) {
        IndexingResponse response = new IndexingResponse();
        if (path.isBlank()) {
            response.setResult(false);
            response.setError("Ссылка отсутствует");
            return response;
        }
        String url = PathUtil.getOriginalPath(path);

        if (siteRepository.findAll().stream().noneMatch(s -> s.getUrl().equals(url))) {
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

            PageDTO pageDTO = PageDTO.builder()
                    .siteDTO(siteMapper.mapToDTO(siteRepository.findByUrl(url)))
                    .code(getStatusCode(path))
                    .content(siteConnection.getDocument().text())
                    .path(path)
                    .build();

            storageComponent.savePage(pageDTO);

        }
        response.setResult(true);
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
