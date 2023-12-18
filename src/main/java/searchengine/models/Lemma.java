package searchengine.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "lemmas")
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Site site;

    @Column(nullable = false)
    private String lemma;

    @Column(nullable = false)
    private int frequency;
}
