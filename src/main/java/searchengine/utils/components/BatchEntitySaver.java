package searchengine.utils.components;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchEntitySaver {

    @Transactional
    public  <T, ID> List<T> saveInBatch(List<T> entities, JpaRepository<T, ID> repository) {
        int batchSize = 100;

        for (int i = 0; i < entities.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, entities.size());
            List<T> sublist = entities.subList(i, endIndex);
            repository.saveAllAndFlush(sublist);
        }

        return entities;
    }
}
