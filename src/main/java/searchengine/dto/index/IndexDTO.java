package searchengine.dto.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import searchengine.dto.lemma.LemmaDTO;
import searchengine.dto.page.PageDTO;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class IndexDTO {
    private Integer id;

    private PageDTO pageDTO;

    private LemmaDTO lemmaDTO;

    private float lemmaRank;
}
