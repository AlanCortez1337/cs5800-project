import { 
  useQuery, 
  useMutation, 
  useQueryClient,
  UseQueryOptions,
  UseMutationOptions 
} from '@tanstack/react-query';
import * as ingredientApi from '@/lib/ingredientAPI';
import { CreateIngredientInput, Ingredient, UpdateIngredientInput } from '@/lib/types';

// Query keys for cache management
export const ingredientKeys = {
  all: ['ingredients'] as const,
  lists: () => [...ingredientKeys.all, 'list'] as const,
  list: (filters?: object) => [...ingredientKeys.lists(), filters] as const,
  details: () => [...ingredientKeys.all, 'detail'] as const,
  detail: (id: number) => [...ingredientKeys.details(), id] as const,
};

// GET all ingredients
export const useIngredients = (
  options?: Omit<UseQueryOptions<Ingredient[]>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: ingredientKeys.lists(),
    queryFn: ingredientApi.getAllIngredients,
    ...options,
  });
};

// GET ingredient by ID
export const useIngredient = (
  id: number,
  options?: Omit<UseQueryOptions<Ingredient>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: ingredientKeys.detail(id),
    queryFn: () => ingredientApi.getIngredientById(id),
    enabled: !!id,
    ...options,
  });
};

// POST create ingredient
export const useCreateIngredient = (
  options?: UseMutationOptions<Ingredient, Error, CreateIngredientInput>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ingredientApi.createIngredient,
    onSuccess: (data) => {
      // Invalidate and refetch all ingredient lists
      queryClient.invalidateQueries({ queryKey: ingredientKeys.lists() });
      // Optionally set the new ingredient in cache
      queryClient.setQueryData(ingredientKeys.detail(data.id), data);
    },
    ...options,
  });
};

// PUT update ingredient
export const useUpdateIngredient = (
  options?: UseMutationOptions<Ingredient, Error, UpdateIngredientInput>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ingredientApi.updateIngredient,
    onSuccess: (data, variables) => {
      // Update the specific ingredient in cache
      queryClient.setQueryData(ingredientKeys.detail(variables.id), data);
      // Invalidate all ingredient lists to refetch
      queryClient.invalidateQueries({ queryKey: ingredientKeys.lists() });
    },
    ...options,
  });
};

// DELETE ingredient
export const useDeleteIngredient = (
  options?: UseMutationOptions<void, Error, number>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ingredientApi.deleteIngredient,
    onSuccess: (_, deletedId) => {
      // Remove the ingredient from cache
      queryClient.removeQueries({ queryKey: ingredientKeys.detail(deletedId) });
      // Invalidate all ingredient lists
      queryClient.invalidateQueries({ queryKey: ingredientKeys.lists() });
    },
    ...options,
  });
};