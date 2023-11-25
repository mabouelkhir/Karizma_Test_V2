import { IRecette } from 'app/shared/model/recette.model';

export interface IEtape {
  id?: number;
  description?: string | null;
  recettes?: IRecette[] | null;
}

export const defaultValue: Readonly<IEtape> = {};
