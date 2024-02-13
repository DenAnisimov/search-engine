package searchengine.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@RequiredArgsConstructor
@Table(name = "search_indexes")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Page page;

    @ManyToOne(cascade = CascadeType.ALL)
    private Lemma lemma;

    @Column(name = "lemma_rank", nullable = false)
    private float lemmaRank;
}
