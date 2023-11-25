package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Recette.
 */
@Entity
@Table(name = "recette")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Recette implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "duree_preparation")
    private Integer dureePreparation;

    @ManyToOne
    private User createur;

    @ManyToMany
    @JoinTable(
        name = "rel_recette__etapes",
        joinColumns = @JoinColumn(name = "recette_id"),
        inverseJoinColumns = @JoinColumn(name = "etapes_id")
    )
    @JsonIgnoreProperties(value = { "recettes" }, allowSetters = true)
    private Set<Etape> etapes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_recette__ingredients",
        joinColumns = @JoinColumn(name = "recette_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredients_id")
    )
    @JsonIgnoreProperties(value = { "recettes" }, allowSetters = true)
    private Set<Ingredient> ingredients = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Recette id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Recette nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getDureePreparation() {
        return this.dureePreparation;
    }

    public Recette dureePreparation(Integer dureePreparation) {
        this.setDureePreparation(dureePreparation);
        return this;
    }

    public void setDureePreparation(Integer dureePreparation) {
        this.dureePreparation = dureePreparation;
    }

    public User getCreateur() {
        return this.createur;
    }

    public void setCreateur(User user) {
        this.createur = user;
    }

    public Recette createur(User user) {
        this.setCreateur(user);
        return this;
    }

    public Set<Etape> getEtapes() {
        return this.etapes;
    }

    public void setEtapes(Set<Etape> etapes) {
        this.etapes = etapes;
    }

    public Recette etapes(Set<Etape> etapes) {
        this.setEtapes(etapes);
        return this;
    }

    public Recette addEtapes(Etape etape) {
        this.etapes.add(etape);
        etape.getRecettes().add(this);
        return this;
    }

    public Recette removeEtapes(Etape etape) {
        this.etapes.remove(etape);
        etape.getRecettes().remove(this);
        return this;
    }

    public Set<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Recette ingredients(Set<Ingredient> ingredients) {
        this.setIngredients(ingredients);
        return this;
    }

    public Recette addIngredients(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        ingredient.getRecettes().add(this);
        return this;
    }

    public Recette removeIngredients(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
        ingredient.getRecettes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recette)) {
            return false;
        }
        return id != null && id.equals(((Recette) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Recette{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", dureePreparation=" + getDureePreparation() +
            "}";
    }
}
