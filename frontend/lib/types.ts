export interface User {
  id: number;
  username: string;
  password?: string;
  role: 'ADMIN' | 'STAFF';
}

export interface CreateUserInput {
  userName: string;
  password: string;
  role: 'ADMIN' | 'STAFF';
}

export interface UpdateUserInput {
  id: number;
  username: string;
  password: string;
}

export interface RecipeComponent {
  id?: number;
  name: string;
  quantity: number;
  unit: string;
}

export interface UseHistory {
  id?: number;
  usedDate: string;
  quantity: number;
  notes?: string;
}

export interface Recipe {
  id: number;
  recipeName: string;
  recipeComponents: RecipeComponent[];
  useCount: number;
  useHistory: UseHistory[];
}

export interface CreateRecipeInput {
  recipeName: string;
  recipeComponents: RecipeComponent[];
  useCount?: number;
  useHistory?: UseHistory[];
}

export interface UpdateRecipeInput {
  id: number;
  recipeName?: string;
  recipeComponents?: RecipeComponent[];
  useCount?: number;
  useHistory?: UseHistory[];
}

export interface UnitDetails {
  unitOfMeasurement: string;
  pricePerUnit?: number;
}

export interface QuantityDetails {
  currentQuantity: number;
  maxQuantityLimit?: number;
  alertLowQuantity?: number;
  timesReachedLow?: number;
}

export interface Ingredient {
  ingredientID: number;
  productName: string;
  unitDetails: UnitDetails;
  quantityDetails: QuantityDetails;
  dateAdded?: string;
  dateUpdated?: string;
}

export interface CreateIngredientInput {
  productName: string;
  unitDetails: UnitDetails;
  quantityDetails: QuantityDetails;
}

export interface UpdateIngredientInput {
  ingredientID: number;
  productName?: string;
  unitDetails?: UnitDetails;
  quantityDetails?: QuantityDetails;
}
