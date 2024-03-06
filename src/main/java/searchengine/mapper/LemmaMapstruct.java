package searchengine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import searchengine.dto.lemma.LemmaDTO;
import searchengine.models.Lemma;

import java.util.List;

@Mapper(componentModel = "spring", uses = SiteMapstruct.class)
public interface LemmaMapstruct {
    @Mapping(target = "siteDTO", source = "site")
    LemmaDTO toDTO(Lemma lemma);

    @Mapping(target = "site", source = "siteDTO")
    Lemma toEntity(LemmaDTO lemmaDTO);

    List<Lemma> toEntities(List<LemmaDTO> lemmaDTOs);

    List<LemmaDTO> toDTOs(List<Lemma> lemma);
}
