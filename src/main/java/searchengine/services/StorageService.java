package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.page.PageDTO;
import searchengine.mapper.PageMapper;
import searchengine.models.Page;
import searchengine.repository.PageRepository;

import java.util.LinkedList;
import java.util.Queue;

@RequiredArgsConstructor
@Service
public class StorageService {
    private final PageRepository pageRepository;
    private final PageMapper pageMapper;

    @Transactional
    public void save(Queue<PageDTO> pageDTOQueue) {
        int batchSize = 100;

        LinkedList<Page> batch = new LinkedList<>();
        while (!pageDTOQueue.isEmpty()) {

            PageDTO pageDTO = pageDTOQueue.poll();

            batch.add(pageMapper.mapToEntity(pageDTO));

            if (batch.size() == batchSize || pageDTOQueue.isEmpty()) {
                pageRepository.saveAll(batch);
                batch.clear();
            }
        }
    }
}
