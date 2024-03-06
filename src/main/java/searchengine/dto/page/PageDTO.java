package searchengine.dto.page;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import searchengine.dto.site.SiteDTO;

@RequiredArgsConstructor
@Getter
@SuperBuilder
@ToString
public class PageDTO {
    private SiteDTO siteDTO;

    private String path;

    private int code;

    private String content;
}
