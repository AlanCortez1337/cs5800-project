'use client';

import { useEffect } from 'react';
import { useForm, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useUpdateRecipe } from '@/hooks/useRecipes';
import { useIngredients } from '@/hooks/useIngredients';
import { Loader2, Plus, X } from 'lucide-react';
import { Recipe } from '@/lib/types';
import { toast } from 'sonner';

const componentSchema = z.object({
  ingredientID: z.number().min(1, 'Please select an ingredient'),
  quantity: z.number().min(1, 'Quantity must be at least 1'),
});

const formSchema = z.object({
  recipeName: z.string().min(2, {
    message: 'Recipe name must be at least 2 characters.',
  }),
  recipeComponents: z.array(componentSchema).min(1, 'At least one component is required'),
  useCount: z.number(),
});

type FormValues = z.infer<typeof formSchema>;

interface EditRecipeDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  recipe: Recipe | null;
}

export function EditRecipeDialog({
  open,
  onOpenChange,
  recipe,
}: EditRecipeDialogProps) {
  const { data: ingredients } = useIngredients();

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      recipeName: '',
      recipeComponents: [{ ingredientID: 0, quantity: 1 }],
      useCount: 0,
    },
  });

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: 'recipeComponents',
  });

  useEffect(() => {
    if (recipe) {
      form.reset({
        recipeName: recipe.recipeName,
        recipeComponents: recipe.recipeComponents?.map(comp => ({
          ingredientID: comp.ingredient.ingredientID,
          quantity: comp.quantity,
        })) || [{ ingredientID: 0, quantity: 1 }],
        useCount: recipe.useCount,
      });
    }
  }, [recipe, form]);

  const updateRecipeMutation = useUpdateRecipe({
    onSuccess: () => {
      onOpenChange(false);
      toast.success('Recipe updated successfully!');
    },
    onError: (error) => {
      toast.error(`Failed to update recipe: ${error.message}`);
    },
  });

  function onSubmit(values: FormValues) {
    if (!recipe) return;

    updateRecipeMutation.mutate({
      recipeID: recipe.recipeID,
      recipeName: values.recipeName,
      recipeComponents: values.recipeComponents.map(comp => ({
        ingredient: { ingredientID: comp.ingredientID },
        quantity: comp.quantity,
      })),
      useCount: values.useCount,
      useHistory: recipe.useHistory,
    });
  }

  if (!recipe) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px] max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Edit Recipe</DialogTitle>
          <DialogDescription>
            Make changes to your recipe. Click save when {"you're"} done.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="recipeName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Recipe Name</FormLabel>
                  <FormControl>
                    <Input placeholder="e.g., Chocolate Chip Cookies" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="useCount"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Times Used</FormLabel>
                  <FormControl>
                    <Input
                      type="number"
                      {...field}
                      onChange={(e) => field.onChange(Number(e.target.value))}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <FormLabel>Components</FormLabel>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => append({ ingredientID: 0, quantity: 1 })}
                >
                  <Plus className="h-4 w-4 mr-1" />
                  Add Component
                </Button>
              </div>

              {fields.map((field, index) => (
                <div key={field.id} className="flex gap-2 items-start p-3 border rounded-lg">
                  <div className="flex-1 grid grid-cols-2 gap-2">
                    <FormField
                      control={form.control}
                      name={`recipeComponents.${index}.ingredientID`}
                      render={({ field }) => (
                        <FormItem>
                          <Select
                            onValueChange={(value) => field.onChange(Number(value))}
                            value={field.value?.toString() || ''}
                          >
                            <FormControl>
                              <SelectTrigger>
                                <SelectValue placeholder="Select ingredient" />
                              </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                              {ingredients?.map((ing) => (
                                <SelectItem key={ing.ingredientID} value={ing.ingredientID.toString()}>
                                  {ing.productName}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name={`recipeComponents.${index}.quantity`}
                      render={({ field }) => (
                        <FormItem>
                          <FormControl>
                            <Input
                              type="number"
                              placeholder="Quantity"
                              {...field}
                              onChange={(e) => field.onChange(Number(e.target.value))}
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                  {fields.length > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      onClick={() => remove(index)}
                    >
                      <X className="h-4 w-4" />
                    </Button>
                  )}
                </div>
              ))}
            </div>

            {recipe.useHistory && recipe.useHistory.length > 0 && (
              <div className="rounded-md bg-blue-50 p-3 text-sm">
                <p className="text-blue-800 font-semibold mb-1">
                  Usage History ({recipe.useHistory.length} records)
                </p>
                <div className="text-blue-700 text-xs space-y-1">
                  {recipe.useHistory.slice(0, 3).map((history, idx) => (
                    <div key={idx}>
                      {new Date(history.lastUsed).toLocaleDateString()}
                    </div>
                  ))}
                  {recipe.useHistory.length > 3 && (
                    <div>... and {recipe.useHistory.length - 3} more</div>
                  )}
                </div>
              </div>
            )}

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={updateRecipeMutation.isPending}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={updateRecipeMutation.isPending}>
                {updateRecipeMutation.isPending && (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                )}
                Save Changes
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}