package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Recette;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class RecetteRepositoryWithBagRelationshipsImpl implements RecetteRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Recette> fetchBagRelationships(Optional<Recette> recette) {
        return recette.map(this::fetchEtapes).map(this::fetchIngredients);
    }

    @Override
    public Page<Recette> fetchBagRelationships(Page<Recette> recettes) {
        return new PageImpl<>(fetchBagRelationships(recettes.getContent()), recettes.getPageable(), recettes.getTotalElements());
    }

    @Override
    public List<Recette> fetchBagRelationships(List<Recette> recettes) {
        return Optional.of(recettes).map(this::fetchEtapes).map(this::fetchIngredients).orElse(Collections.emptyList());
    }

    Recette fetchEtapes(Recette result) {
        return entityManager
            .createQuery("select recette from Recette recette left join fetch recette.etapes where recette is :recette", Recette.class)
            .setParameter("recette", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Recette> fetchEtapes(List<Recette> recettes) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, recettes.size()).forEach(index -> order.put(recettes.get(index).getId(), index));
        List<Recette> result = entityManager
            .createQuery(
                "select distinct recette from Recette recette left join fetch recette.etapes where recette in :recettes",
                Recette.class
            )
            .setParameter("recettes", recettes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Recette fetchIngredients(Recette result) {
        return entityManager
            .createQuery("select recette from Recette recette left join fetch recette.ingredients where recette is :recette", Recette.class)
            .setParameter("recette", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Recette> fetchIngredients(List<Recette> recettes) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, recettes.size()).forEach(index -> order.put(recettes.get(index).getId(), index));
        List<Recette> result = entityManager
            .createQuery(
                "select distinct recette from Recette recette left join fetch recette.ingredients where recette in :recettes",
                Recette.class
            )
            .setParameter("recettes", recettes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
