import { 
  useQuery, 
  useMutation, 
  useQueryClient,
  UseQueryOptions,
  UseMutationOptions 
} from '@tanstack/react-query';
import * as ingredientApi from '@/lib/ingredientAPI';
import type { Ingredient, CreateIngredientInput, UpdateIngredientInput } from '@/lib/types';

export const ingredientKeys = {
  all: ['ingredients'] as const,
  lists: () => [...ingredientKeys.all, 'list'] as const,
  list: (filters?: object) => [...ingredientKeys.lists(), filters] as const,
  details: () => [...ingredientKeys.all, 'detail'] as const,
  detail: (id: number) => [...ingredientKeys.details(), id] as const,
};

export const useIngredients = (
  options?: Omit<UseQueryOptions<Ingredient[]>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: ingredientKeys.lists(),
    queryFn: ingredientApi.getAllIngredients,
    ...options,
  });
};

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

export const useCreateIngredient = (
  options?: UseMutationOptions<Ingredient, Error, CreateIngredientInput>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ingredientApi.createIngredient,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ingredientKeys.lists() });
      queryClient.setQueryData(ingredientKeys.detail(data.ingredientID), data);
    },
    ...options,
  });
};

export const useUpdateIngredient = (
  options?: UseMutationOptions<Ingredient, Error, UpdateIngredientInput>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ingredientApi.updateIngredient,
    onSuccess: (data, variables) => {
      queryClient.setQueryData(ingredientKeys.detail(variables.ingredientID), data);
      queryClient.invalidateQueries({ queryKey: ingredientKeys.lists() });
    },
    ...options,
  });
};

export const useDeleteIngredient = (
  options?: UseMutationOptions<void, Error, number>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ingredientApi.deleteIngredient,
    onSuccess: (_, deletedId) => {
      queryClient.removeQueries({ queryKey: ingredientKeys.detail(deletedId) });
      queryClient.invalidateQueries({ queryKey: ingredientKeys.lists() });
    },
    ...options,
  });
};