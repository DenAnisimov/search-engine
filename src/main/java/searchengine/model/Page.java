package searchengine.model;

import jakarta.persistence.Index;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pages", indexes = @Index(name = "path_index", columnList = "path"))
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Site site;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(nullable = false)
    private int code;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;
}
