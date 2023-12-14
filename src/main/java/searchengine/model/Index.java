package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "search_indexes")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Page page;

    @ManyToOne
    private Lemma lemma;

    @Column(name = "lemma_rank", nullable = false)
    private float lemmaRank;
}
