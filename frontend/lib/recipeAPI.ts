import { CreateRecipeInput, Recipe, UpdateRecipeInput } from "./types";

// GET all recipes
export const getAllRecipes = async (): Promise<Recipe[]> => {
  const response = await fetch('/api/recipes');
  if (!response.ok) throw new Error('Failed to fetch recipes');
  return response.json();
};

// GET recipe by ID
export const getRecipeById = async (id: number): Promise<Recipe> => {
  const response = await fetch(`/api/recipes?id=${id}`);
  if (!response.ok) throw new Error('Failed to fetch recipe');
  return response.json();
};

// POST create recipe
export const createRecipe = async (data: CreateRecipeInput): Promise<Recipe> => {
  const response = await fetch('/api/recipes', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to create recipe');
  return response.json();
};

// PUT update recipe
export const updateRecipe = async (data: UpdateRecipeInput): Promise<Recipe> => {
  const response = await fetch('/api/recipes', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to update recipe');
  return response.json();
};

// DELETE recipe
export const deleteRecipe = async (id: number): Promise<void> => {
  const response = await fetch(`/api/recipes?id=${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error('Failed to delete recipe');
};