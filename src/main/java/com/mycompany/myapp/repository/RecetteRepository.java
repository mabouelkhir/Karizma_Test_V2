package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Recette;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Recette entity.
 *
 * When extending this class, extend RecetteRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface RecetteRepository extends RecetteRepositoryWithBagRelationships, JpaRepository<Recette, Long> {
    @Query("select recette from Recette recette where recette.createur.login = ?#{principal.username}")
    List<Recette> findByCreateurIsCurrentUser();

    default Optional<Recette> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Recette> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Recette> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
