package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Ingredient.
 */
@Entity
@Table(name = "ingredient")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ingredient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @ManyToMany(mappedBy = "ingredients")
    @JsonIgnoreProperties(value = { "createur", "etapes", "ingredients" }, allowSetters = true)
    private Set<Recette> recettes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ingredient id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Ingredient nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<Recette> getRecettes() {
        return this.recettes;
    }

    public void setRecettes(Set<Recette> recettes) {
        if (this.recettes != null) {
            this.recettes.forEach(i -> i.removeIngredients(this));
        }
        if (recettes != null) {
            recettes.forEach(i -> i.addIngredients(this));
        }
        this.recettes = recettes;
    }

    public Ingredient recettes(Set<Recette> recettes) {
        this.setRecettes(recettes);
        return this;
    }

    public Ingredient addRecette(Recette recette) {
        this.recettes.add(recette);
        recette.getIngredients().add(this);
        return this;
    }

    public Ingredient removeRecette(Recette recette) {
        this.recettes.remove(recette);
        recette.getIngredients().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ingredient)) {
            return false;
        }
        return id != null && id.equals(((Ingredient) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ingredient{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
