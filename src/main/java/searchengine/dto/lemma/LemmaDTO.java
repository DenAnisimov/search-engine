package searchengine.dto.lemma;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import searchengine.dto.site.SiteDTO;
import searchengine.models.Lemma;
import searchengine.models.Site;

@Getter
@SuperBuilder
@Setter
@RequiredArgsConstructor
public class LemmaDTO {
    private Integer id;

    private SiteDTO siteDTO;

    private String lemma;

    private int frequency;

    /* todo: Дописать логику*/
    private boolean isLemmaBelongToSite(Lemma lemma) {
        return false;
    }
}
