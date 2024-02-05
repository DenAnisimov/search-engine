package searchengine.dto.index;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import searchengine.dto.lemma.LemmaDTO;
import searchengine.dto.page.PageDTO;
import searchengine.models.Lemma;
import searchengine.models.Page;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class IndexDTO {
    private int id;

    private PageDTO pageDTO;

    private LemmaDTO lemmaDTO;

    private float lemmaRank;
}
