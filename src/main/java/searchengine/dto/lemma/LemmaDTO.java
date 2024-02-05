package searchengine.dto.lemma;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import searchengine.dto.site.SiteDTO;
import searchengine.models.Site;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class LemmaDTO {
    private int id;

    private SiteDTO siteDTO;

    private String lemma;

    private int frequency;
}
