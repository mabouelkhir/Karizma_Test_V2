import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IRecette } from 'app/shared/model/recette.model';
import { getEntities } from './recette.reducer';

export const Recette = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const recetteList = useAppSelector(state => state.recette.entities);
  const loading = useAppSelector(state => state.recette.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="recette-heading" data-cy="RecetteHeading">
        Recettes
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Actualiser la liste
          </Button>
          <Link to="/recette/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Créer un nouveau Recette
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {recetteList && recetteList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>Id</th>
                <th>Nom</th>
                <th>Duree Preparation</th>
                <th>Createur</th>
                <th>Etapes</th>
                <th>Ingredients</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {recetteList.map((recette, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/recette/${recette.id}`} color="link" size="sm">
                      {recette.id}
                    </Button>
                  </td>
                  <td>{recette.nom}</td>
                  <td>{recette.dureePreparation}</td>
                  <td>{recette.createur ? recette.createur.id : ''}</td>
                  <td>
                    {recette.etapes
                      ? recette.etapes.map((val, j) => (
                          <span key={j}>
                            <Link to={`/etape/${val.id}`}>{val.id}</Link>
                            {j === recette.etapes.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td>
                    {recette.ingredients
                      ? recette.ingredients.map((val, j) => (
                          <span key={j}>
                            <Link to={`/ingredient/${val.id}`}>{val.id}</Link>
                            {j === recette.ingredients.length - 1 ? '' : ', '}
                          </span>
                        ))
                      : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/recette/${recette.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Voir</span>
                      </Button>
                      <Button tag={Link} to={`/recette/${recette.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editer</span>
                      </Button>
                      <Button tag={Link} to={`/recette/${recette.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Supprimer</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">Aucun Recette trouvé</div>
        )}
      </div>
    </div>
  );
};

export default Recette;
