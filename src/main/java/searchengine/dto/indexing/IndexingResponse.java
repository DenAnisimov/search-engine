package searchengine.dto.indexing;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class IndexingResponse {
    private boolean result;
    private String error;
}
