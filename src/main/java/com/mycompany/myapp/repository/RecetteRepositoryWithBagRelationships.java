package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Recette;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface RecetteRepositoryWithBagRelationships {
    Optional<Recette> fetchBagRelationships(Optional<Recette> recette);

    List<Recette> fetchBagRelationships(List<Recette> recettes);

    Page<Recette> fetchBagRelationships(Page<Recette> recettes);
}
