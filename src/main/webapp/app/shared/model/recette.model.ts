import { IUser } from 'app/shared/model/user.model';
import { IEtape } from 'app/shared/model/etape.model';
import { IIngredient } from 'app/shared/model/ingredient.model';

export interface IRecette {
  id?: number;
  nom?: string | null;
  dureePreparation?: number | null;
  createur?: IUser | null;
  etapes?: IEtape[] | null;
  ingredients?: IIngredient[] | null;
}

export const defaultValue: Readonly<IRecette> = {};
