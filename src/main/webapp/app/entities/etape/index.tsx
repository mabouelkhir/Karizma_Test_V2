import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Etape from './etape';
import EtapeDetail from './etape-detail';
import EtapeUpdate from './etape-update';
import EtapeDeleteDialog from './etape-delete-dialog';

const EtapeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Etape />} />
    <Route path="new" element={<EtapeUpdate />} />
    <Route path=":id">
      <Route index element={<EtapeDetail />} />
      <Route path="edit" element={<EtapeUpdate />} />
      <Route path="delete" element={<EtapeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EtapeRoutes;
