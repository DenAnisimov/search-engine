package searchengine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import searchengine.dto.index.IndexDTO;
import searchengine.models.Index;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PageMapstruct.class, LemmaMapstruct.class})
public interface IndexMapstruct {
    @Mapping(source = "page", target = "pageDTO")
    @Mapping(source = "lemma", target = "lemmaDTO")
    IndexDTO toDTO(Index index);

    @Mapping(source = "pageDTO", target = "page")
    @Mapping(source = "lemmaDTO", target = "lemma")
    Index toModel(IndexDTO indexDTO);

    List<Index> toModels(List<IndexDTO> indexes);
}
