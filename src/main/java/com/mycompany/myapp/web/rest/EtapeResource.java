package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Etape;
import com.mycompany.myapp.repository.EtapeRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Etape}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EtapeResource {

    private final Logger log = LoggerFactory.getLogger(EtapeResource.class);

    private static final String ENTITY_NAME = "etape";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EtapeRepository etapeRepository;

    public EtapeResource(EtapeRepository etapeRepository) {
        this.etapeRepository = etapeRepository;
    }

    /**
     * {@code POST  /etapes} : Create a new etape.
     *
     * @param etape the etape to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etape, or with status {@code 400 (Bad Request)} if the etape has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/etapes")
    public ResponseEntity<Etape> createEtape(@RequestBody Etape etape) throws URISyntaxException {
        log.debug("REST request to save Etape : {}", etape);
        if (etape.getId() != null) {
            throw new BadRequestAlertException("A new etape cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Etape result = etapeRepository.save(etape);
        return ResponseEntity
            .created(new URI("/api/etapes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /etapes/:id} : Updates an existing etape.
     *
     * @param id the id of the etape to save.
     * @param etape the etape to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etape,
     * or with status {@code 400 (Bad Request)} if the etape is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etape couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/etapes/{id}")
    public ResponseEntity<Etape> updateEtape(@PathVariable(value = "id", required = false) final Long id, @RequestBody Etape etape)
        throws URISyntaxException {
        log.debug("REST request to update Etape : {}, {}", id, etape);
        if (etape.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etape.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etapeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Etape result = etapeRepository.save(etape);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, etape.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /etapes/:id} : Partial updates given fields of an existing etape, field will ignore if it is null
     *
     * @param id the id of the etape to save.
     * @param etape the etape to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etape,
     * or with status {@code 400 (Bad Request)} if the etape is not valid,
     * or with status {@code 404 (Not Found)} if the etape is not found,
     * or with status {@code 500 (Internal Server Error)} if the etape couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/etapes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Etape> partialUpdateEtape(@PathVariable(value = "id", required = false) final Long id, @RequestBody Etape etape)
        throws URISyntaxException {
        log.debug("REST request to partial update Etape partially : {}, {}", id, etape);
        if (etape.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etape.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etapeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Etape> result = etapeRepository
            .findById(etape.getId())
            .map(existingEtape -> {
                if (etape.getDescription() != null) {
                    existingEtape.setDescription(etape.getDescription());
                }

                return existingEtape;
            })
            .map(etapeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, etape.getId().toString())
        );
    }

    /**
     * {@code GET  /etapes} : get all the etapes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of etapes in body.
     */
    @GetMapping("/etapes")
    public List<Etape> getAllEtapes() {
        log.debug("REST request to get all Etapes");
        return etapeRepository.findAll();
    }

    /**
     * {@code GET  /etapes/:id} : get the "id" etape.
     *
     * @param id the id of the etape to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etape, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/etapes/{id}")
    public ResponseEntity<Etape> getEtape(@PathVariable Long id) {
        log.debug("REST request to get Etape : {}", id);
        Optional<Etape> etape = etapeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(etape);
    }

    /**
     * {@code DELETE  /etapes/:id} : delete the "id" etape.
     *
     * @param id the id of the etape to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/etapes/{id}")
    public ResponseEntity<Void> deleteEtape(@PathVariable Long id) {
        log.debug("REST request to delete Etape : {}", id);
        etapeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
