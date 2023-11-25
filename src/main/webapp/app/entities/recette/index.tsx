import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Recette from './recette';
import RecetteDetail from './recette-detail';
import RecetteUpdate from './recette-update';
import RecetteDeleteDialog from './recette-delete-dialog';

const RecetteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Recette />} />
    <Route path="new" element={<RecetteUpdate />} />
    <Route path=":id">
      <Route index element={<RecetteDetail />} />
      <Route path="edit" element={<RecetteUpdate />} />
      <Route path="delete" element={<RecetteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RecetteRoutes;
