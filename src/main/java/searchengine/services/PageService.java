package searchengine.services;

import searchengine.models.Page;
import searchengine.repository.PageRepository;

import java.util.List;

public class PageService {
    private final PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public List<Page> getAllPages() {
        return pageRepository.findAll();
    }

    public Page getPageById(int id) {
        return pageRepository.getReferenceById(id);
    }

    public List<Page> getPagesBySiteId(int id) {
        return pageRepository.findAllBySiteId(id);
    }

    public Page savePage(Page page) {
        return pageRepository.save(page);
    }

    public void deletePage(Page page) {
        pageRepository.delete(page);
    }
}
