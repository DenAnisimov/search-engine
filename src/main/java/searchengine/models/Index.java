package searchengine.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
@Table(name = "search_indexes")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Page page;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;

    @Column(name = "lemma_rank", nullable = false)
    private float lemmaRank;
}
