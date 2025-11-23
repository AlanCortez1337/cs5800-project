import { 
  useQuery, 
  useMutation, 
  useQueryClient,
  UseQueryOptions,
  UseMutationOptions 
} from '@tanstack/react-query';
import * as recipeApi from '@/lib/recipeAPI';
import { CreateRecipeInput, Recipe, UpdateRecipeInput } from '@/lib/types';

// Query keys for cache management
export const recipeKeys = {
  all: ['recipes'] as const,
  lists: () => [...recipeKeys.all, 'list'] as const,
  list: (filters?: object) => [...recipeKeys.lists(), filters] as const,
  details: () => [...recipeKeys.all, 'detail'] as const,
  detail: (recipeID: number) => [...recipeKeys.details(), recipeID] as const,
};

// GET all recipes
export const useRecipes = (
  options?: Omit<UseQueryOptions<Recipe[]>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: recipeKeys.lists(),
    queryFn: recipeApi.getAllRecipes,
    ...options,
  });
};

// GET recipe by ID
export const useRecipe = (
  recipeID: number,
  options?: Omit<UseQueryOptions<Recipe>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: recipeKeys.detail(recipeID),
    queryFn: () => recipeApi.getRecipeById(recipeID),
    enabled: !!recipeID,
    ...options,
  });
};

// POST create recipe
export const useCreateRecipe = (
  options?: UseMutationOptions<Recipe, Error, CreateRecipeInput>
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: recipeApi.createRecipe,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: recipeKeys.lists() });
      queryClient.setQueryData(recipeKeys.detail(data.recipeID), data);  // Changed
    },
    ...options,
  });
};

// PUT update recipe
export const useUpdateRecipe = (
  options?: UseMutationOptions<Recipe, Error, UpdateRecipeInput>
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: recipeApi.updateRecipe,
    onSuccess: (data, variables) => {
      queryClient.setQueryData(recipeKeys.detail(variables.recipeID), data);  // Changed
      queryClient.invalidateQueries({ queryKey: recipeKeys.lists() });
    },
    ...options,
  });
};

// DELETE recipe
export const useDeleteRecipe = (
  options?: UseMutationOptions<void, Error, number>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: recipeApi.deleteRecipe,
    onSuccess: (_, deletedId) => {
      // Remove the recipe from cache
      queryClient.removeQueries({ queryKey: recipeKeys.detail(deletedId) });
      // Invalidate all recipe lists
      queryClient.invalidateQueries({ queryKey: recipeKeys.lists() });
    },
    ...options,
  });
};