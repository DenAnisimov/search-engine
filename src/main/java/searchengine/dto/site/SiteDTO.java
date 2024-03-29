package searchengine.dto.site;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import searchengine.models.enums.Status;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@SuperBuilder
@ToString
public class SiteDTO {
    private int id;
    private Status status;
    private LocalDateTime statusTime;
    private String lastError;
    private String url;
    private String name;
}
