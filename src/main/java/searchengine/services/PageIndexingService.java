package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.page.PageDTO;
import searchengine.mapper.SiteMapstruct;
import searchengine.models.Page;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.utils.PathUtil;
import searchengine.utils.SiteConnection;
import searchengine.utils.components.PageComponent;
import searchengine.utils.components.StorageCleanerComponent;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageIndexingService {
    private final SiteRepository siteRepository;
    private final SiteMapstruct siteMapstruct;
    private final PageRepository pageRepository;
    private final PageComponent pageComponent;
    private final StorageCleanerComponent storageCleanerComponent;

    public IndexingResponse indexPage(String path) {
        IndexingResponse response = new IndexingResponse();
        if (path.isBlank()) {
            response.setResult(false);
            response.setError("Ссылка отсутствует");
            return response;
        }
        SiteConnection siteConnection = new SiteConnection(path);
        String url = PathUtil.getOriginalPath(path);

        if (siteRepository.findAll().stream().noneMatch(s -> s.getUrl().equals(url))) {
            response.setResult(false);
            response.setError("Данная страница находится за пределами сайтов," +
                    "указанных в конфигурационном файле");
            return response;
        }

        if (siteConnection.getStatusCode() <= 400) {
            Page page = pageRepository.findByPath(path);
            if (page != null) {
                storageCleanerComponent.deletePage(page);
                log.info("Транзакция удаления зокнчилась");
            }

            PageDTO pageDTO = PageDTO.builder()
                    .siteDTO(siteMapstruct.toDTO(siteRepository.findByUrl(url)))
                    .code(siteConnection.getStatusCode())
                    .content(siteConnection.getDocument().text())
                    .path(path)
                    .build();

            pageComponent.savePage(pageDTO);

        }
        response.setResult(true);
        return new IndexingResponse();
    }
}
