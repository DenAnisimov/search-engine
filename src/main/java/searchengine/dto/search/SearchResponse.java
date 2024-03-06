package searchengine.dto.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    private boolean result;
    private String error;
    private int count;
    private int offset;
    private int limit;
    private List<DetailedSearchItem> data;
}
