import { IRecette } from 'app/shared/model/recette.model';

export interface IIngredient {
  id?: number;
  nom?: string | null;
  recettes?: IRecette[] | null;
}

export const defaultValue: Readonly<IIngredient> = {};
