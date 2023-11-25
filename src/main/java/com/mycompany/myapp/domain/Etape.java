package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Etape.
 */
@Entity
@Table(name = "etape")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Etape implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "etapes")
    @JsonIgnoreProperties(value = { "createur", "etapes", "ingredients" }, allowSetters = true)
    private Set<Recette> recettes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Etape id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Etape description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Recette> getRecettes() {
        return this.recettes;
    }

    public void setRecettes(Set<Recette> recettes) {
        if (this.recettes != null) {
            this.recettes.forEach(i -> i.removeEtapes(this));
        }
        if (recettes != null) {
            recettes.forEach(i -> i.addEtapes(this));
        }
        this.recettes = recettes;
    }

    public Etape recettes(Set<Recette> recettes) {
        this.setRecettes(recettes);
        return this;
    }

    public Etape addRecette(Recette recette) {
        this.recettes.add(recette);
        recette.getEtapes().add(this);
        return this;
    }

    public Etape removeRecette(Recette recette) {
        this.recettes.remove(recette);
        recette.getEtapes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Etape)) {
            return false;
        }
        return id != null && id.equals(((Etape) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Etape{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
