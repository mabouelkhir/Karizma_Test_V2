package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Recette;
import com.mycompany.myapp.repository.RecetteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RecetteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RecetteResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Integer DEFAULT_DUREE_PREPARATION = 1;
    private static final Integer UPDATED_DUREE_PREPARATION = 2;

    private static final String ENTITY_API_URL = "/api/recettes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RecetteRepository recetteRepository;

    @Mock
    private RecetteRepository recetteRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecetteMockMvc;

    private Recette recette;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recette createEntity(EntityManager em) {
        Recette recette = new Recette().nom(DEFAULT_NOM).dureePreparation(DEFAULT_DUREE_PREPARATION);
        return recette;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recette createUpdatedEntity(EntityManager em) {
        Recette recette = new Recette().nom(UPDATED_NOM).dureePreparation(UPDATED_DUREE_PREPARATION);
        return recette;
    }

    @BeforeEach
    public void initTest() {
        recette = createEntity(em);
    }

    @Test
    @Transactional
    void createRecette() throws Exception {
        int databaseSizeBeforeCreate = recetteRepository.findAll().size();
        // Create the Recette
        restRecetteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recette)))
            .andExpect(status().isCreated());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeCreate + 1);
        Recette testRecette = recetteList.get(recetteList.size() - 1);
        assertThat(testRecette.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testRecette.getDureePreparation()).isEqualTo(DEFAULT_DUREE_PREPARATION);
    }

    @Test
    @Transactional
    void createRecetteWithExistingId() throws Exception {
        // Create the Recette with an existing ID
        recette.setId(1L);

        int databaseSizeBeforeCreate = recetteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecetteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recette)))
            .andExpect(status().isBadRequest());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRecettes() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        // Get all the recetteList
        restRecetteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recette.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].dureePreparation").value(hasItem(DEFAULT_DUREE_PREPARATION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRecettesWithEagerRelationshipsIsEnabled() throws Exception {
        when(recetteRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRecetteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(recetteRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRecettesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(recetteRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRecetteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(recetteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRecette() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        // Get the recette
        restRecetteMockMvc
            .perform(get(ENTITY_API_URL_ID, recette.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recette.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.dureePreparation").value(DEFAULT_DUREE_PREPARATION));
    }

    @Test
    @Transactional
    void getNonExistingRecette() throws Exception {
        // Get the recette
        restRecetteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRecette() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();

        // Update the recette
        Recette updatedRecette = recetteRepository.findById(recette.getId()).get();
        // Disconnect from session so that the updates on updatedRecette are not directly saved in db
        em.detach(updatedRecette);
        updatedRecette.nom(UPDATED_NOM).dureePreparation(UPDATED_DUREE_PREPARATION);

        restRecetteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRecette.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRecette))
            )
            .andExpect(status().isOk());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
        Recette testRecette = recetteList.get(recetteList.size() - 1);
        assertThat(testRecette.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testRecette.getDureePreparation()).isEqualTo(UPDATED_DUREE_PREPARATION);
    }

    @Test
    @Transactional
    void putNonExistingRecette() throws Exception {
        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();
        recette.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecetteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recette.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recette))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecette() throws Exception {
        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();
        recette.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecetteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recette))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecette() throws Exception {
        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();
        recette.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecetteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recette)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRecetteWithPatch() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();

        // Update the recette using partial update
        Recette partialUpdatedRecette = new Recette();
        partialUpdatedRecette.setId(recette.getId());

        restRecetteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecette.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecette))
            )
            .andExpect(status().isOk());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
        Recette testRecette = recetteList.get(recetteList.size() - 1);
        assertThat(testRecette.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testRecette.getDureePreparation()).isEqualTo(DEFAULT_DUREE_PREPARATION);
    }

    @Test
    @Transactional
    void fullUpdateRecetteWithPatch() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();

        // Update the recette using partial update
        Recette partialUpdatedRecette = new Recette();
        partialUpdatedRecette.setId(recette.getId());

        partialUpdatedRecette.nom(UPDATED_NOM).dureePreparation(UPDATED_DUREE_PREPARATION);

        restRecetteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecette.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecette))
            )
            .andExpect(status().isOk());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
        Recette testRecette = recetteList.get(recetteList.size() - 1);
        assertThat(testRecette.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testRecette.getDureePreparation()).isEqualTo(UPDATED_DUREE_PREPARATION);
    }

    @Test
    @Transactional
    void patchNonExistingRecette() throws Exception {
        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();
        recette.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecetteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recette.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recette))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecette() throws Exception {
        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();
        recette.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecetteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recette))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecette() throws Exception {
        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();
        recette.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecetteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(recette)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRecette() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        int databaseSizeBeforeDelete = recetteRepository.findAll().size();

        // Delete the recette
        restRecetteMockMvc
            .perform(delete(ENTITY_API_URL_ID, recette.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
