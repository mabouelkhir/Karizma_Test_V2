import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Recette from './recette';
import Etape from './etape';
import Ingredient from './ingredient';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="recette/*" element={<Recette />} />
        <Route path="etape/*" element={<Etape />} />
        <Route path="ingredient/*" element={<Ingredient />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
