package searchengine.dto.lemma;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import searchengine.dto.site.SiteDTO;

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
    public boolean isLemmaBelongToSite(LemmaDTO lemma) {
        return this.lemma.equals(lemma.getLemma()) &&
                this.siteDTO.getId() == lemma.getSiteDTO().getId();
    }
}
