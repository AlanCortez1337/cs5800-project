import { CreateIngredientInput, Ingredient, UpdateIngredientInput } from "@/lib/types";

// GET all ingredients
export const getAllIngredients = async (): Promise<Ingredient[]> => {
  const response = await fetch('/api/ingredients');
  if (!response.ok) throw new Error('Failed to fetch ingredients');
  return response.json();
};

// GET ingredient by ID
export const getIngredientById = async (id: number): Promise<Ingredient> => {
  const response = await fetch(`/api/ingredients?id=${id}`);
  if (!response.ok) throw new Error('Failed to fetch ingredient');
  return response.json();
};

// POST create ingredient
export const createIngredient = async (data: CreateIngredientInput): Promise<Ingredient> => {
  const response = await fetch('/api/ingredients', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to create ingredient');
  return response.json();
};

// PUT update ingredient
export const updateIngredient = async (data: UpdateIngredientInput): Promise<Ingredient> => {
  const response = await fetch('/api/ingredients', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to update ingredient');
  return response.json();
};

// DELETE ingredient
export const deleteIngredient = async (id: number): Promise<void> => {
  const response = await fetch(`/api/ingredients?id=${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error('Failed to delete ingredient');
};