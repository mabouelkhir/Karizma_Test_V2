package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Etape;
import com.mycompany.myapp.repository.EtapeRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EtapeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EtapeResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/etapes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EtapeRepository etapeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEtapeMockMvc;

    private Etape etape;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etape createEntity(EntityManager em) {
        Etape etape = new Etape().description(DEFAULT_DESCRIPTION);
        return etape;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etape createUpdatedEntity(EntityManager em) {
        Etape etape = new Etape().description(UPDATED_DESCRIPTION);
        return etape;
    }

    @BeforeEach
    public void initTest() {
        etape = createEntity(em);
    }

    @Test
    @Transactional
    void createEtape() throws Exception {
        int databaseSizeBeforeCreate = etapeRepository.findAll().size();
        // Create the Etape
        restEtapeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(etape)))
            .andExpect(status().isCreated());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeCreate + 1);
        Etape testEtape = etapeList.get(etapeList.size() - 1);
        assertThat(testEtape.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createEtapeWithExistingId() throws Exception {
        // Create the Etape with an existing ID
        etape.setId(1L);

        int databaseSizeBeforeCreate = etapeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEtapeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(etape)))
            .andExpect(status().isBadRequest());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEtapes() throws Exception {
        // Initialize the database
        etapeRepository.saveAndFlush(etape);

        // Get all the etapeList
        restEtapeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etape.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getEtape() throws Exception {
        // Initialize the database
        etapeRepository.saveAndFlush(etape);

        // Get the etape
        restEtapeMockMvc
            .perform(get(ENTITY_API_URL_ID, etape.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(etape.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingEtape() throws Exception {
        // Get the etape
        restEtapeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEtape() throws Exception {
        // Initialize the database
        etapeRepository.saveAndFlush(etape);

        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();

        // Update the etape
        Etape updatedEtape = etapeRepository.findById(etape.getId()).get();
        // Disconnect from session so that the updates on updatedEtape are not directly saved in db
        em.detach(updatedEtape);
        updatedEtape.description(UPDATED_DESCRIPTION);

        restEtapeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEtape.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEtape))
            )
            .andExpect(status().isOk());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
        Etape testEtape = etapeList.get(etapeList.size() - 1);
        assertThat(testEtape.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingEtape() throws Exception {
        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();
        etape.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtapeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etape.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(etape))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEtape() throws Exception {
        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();
        etape.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtapeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(etape))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEtape() throws Exception {
        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();
        etape.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtapeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(etape)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEtapeWithPatch() throws Exception {
        // Initialize the database
        etapeRepository.saveAndFlush(etape);

        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();

        // Update the etape using partial update
        Etape partialUpdatedEtape = new Etape();
        partialUpdatedEtape.setId(etape.getId());

        restEtapeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtape.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEtape))
            )
            .andExpect(status().isOk());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
        Etape testEtape = etapeList.get(etapeList.size() - 1);
        assertThat(testEtape.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateEtapeWithPatch() throws Exception {
        // Initialize the database
        etapeRepository.saveAndFlush(etape);

        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();

        // Update the etape using partial update
        Etape partialUpdatedEtape = new Etape();
        partialUpdatedEtape.setId(etape.getId());

        partialUpdatedEtape.description(UPDATED_DESCRIPTION);

        restEtapeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtape.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEtape))
            )
            .andExpect(status().isOk());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
        Etape testEtape = etapeList.get(etapeList.size() - 1);
        assertThat(testEtape.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingEtape() throws Exception {
        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();
        etape.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtapeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, etape.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(etape))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEtape() throws Exception {
        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();
        etape.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtapeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(etape))
            )
            .andExpect(status().isBadRequest());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEtape() throws Exception {
        int databaseSizeBeforeUpdate = etapeRepository.findAll().size();
        etape.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtapeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(etape)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Etape in the database
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEtape() throws Exception {
        // Initialize the database
        etapeRepository.saveAndFlush(etape);

        int databaseSizeBeforeDelete = etapeRepository.findAll().size();

        // Delete the etape
        restEtapeMockMvc
            .perform(delete(ENTITY_API_URL_ID, etape.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Etape> etapeList = etapeRepository.findAll();
        assertThat(etapeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
