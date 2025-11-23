'use client';

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
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
import { useUpdateIngredient } from '@/hooks/useIngredients';
import { Loader2 } from 'lucide-react';
import { Ingredient } from '@/lib/types';
import { toast } from "sonner"

const formSchema = z.object({
  productName: z.string().min(2, {
    message: 'Product name must be at least 2 characters.',
  }),
  unitOfMeasurement: z.string().min(1, {
    message: 'Unit of measurement is required.',
  }),
  pricePerUnit: z.number().min(0).optional().or(z.literal(undefined)),
  currentQuantity: z.number().min(0, {
    message: 'Current quantity must be at least 0.',
  }),
  maxQuantityLimit: z.number().min(0).optional().or(z.literal(undefined)),
  alertLowQuantity: z.number().min(0).optional().or(z.literal(undefined)),
});

type FormValues = z.infer<typeof formSchema>;

interface EditIngredientDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  ingredient: Ingredient | null;
}

export function EditIngredientDialog({
  open,
  onOpenChange,
  ingredient,
}: EditIngredientDialogProps) {
  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      productName: '',
      unitOfMeasurement: '',
      pricePerUnit: undefined,
      currentQuantity: 0,
      maxQuantityLimit: undefined,
      alertLowQuantity: undefined,
    },
  });

  // Update form when ingredient changes
  useEffect(() => {
    if (ingredient) {
      form.reset({
        productName: ingredient.productName,
        unitOfMeasurement: ingredient.unitDetails.unitOfMeasurement,
        pricePerUnit: ingredient.unitDetails.pricePerUnit ?? undefined,
        currentQuantity: ingredient.quantityDetails.currentQuantity,
        maxQuantityLimit: ingredient.quantityDetails.maxQuantityLimit ?? undefined,
        alertLowQuantity: ingredient.quantityDetails.alertLowQuantity ?? undefined,
      });
    }
  }, [ingredient, form]);

  const updateIngredientMutation = useUpdateIngredient({
    onSuccess: () => {
        onOpenChange(false);
        toast.success('Ingredient updated successfully!');
    },
    onError: (error) => {
        toast.error(`Failed to update ingredient: ${error.message}`);
    },
    });

  function onSubmit(values: FormValues) {
    if (!ingredient) return;

    updateIngredientMutation.mutate({
      ingredientID: ingredient.ingredientID,
      productName: values.productName,
      unitDetails: {
        unitOfMeasurement: values.unitOfMeasurement,
        pricePerUnit: values.pricePerUnit,
      },
      quantityDetails: {
        currentQuantity: values.currentQuantity,
        maxQuantityLimit: values.maxQuantityLimit,
        alertLowQuantity: values.alertLowQuantity,
        timesReachedLow: ingredient.quantityDetails.timesReachedLow ?? 0,
      },
    });
  }

  if (!ingredient) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[525px]">
        <DialogHeader>
          <DialogTitle>Edit Ingredient</DialogTitle>
          <DialogDescription>
            Make changes to your ingredient. Click save when {"you're"} done.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="productName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Product Name</FormLabel>
                  <FormControl>
                    <Input placeholder="e.g., All-Purpose Flour" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="unitOfMeasurement"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Unit of Measurement</FormLabel>
                    <FormControl>
                      <Input placeholder="e.g., kg, lbs, oz" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="pricePerUnit"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Price per Unit ($)</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        placeholder="Optional"
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value;
                          field.onChange(value === '' ? undefined : Number(value));
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <div className="grid grid-cols-3 gap-4">
              <FormField
                control={form.control}
                name="currentQuantity"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Current Quantity</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        {...field}
                        onChange={(e) => field.onChange(Number(e.target.value))}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="maxQuantityLimit"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Max Limit</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        placeholder="Optional"
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value;
                          field.onChange(value === '' ? undefined : Number(value));
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="alertLowQuantity"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Low Alert</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        placeholder="Optional"
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value;
                          field.onChange(value === '' ? undefined : Number(value));
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            {ingredient.quantityDetails.timesReachedLow !== undefined && 
             ingredient.quantityDetails.timesReachedLow > 0 && (
              <div className="rounded-md bg-yellow-50 p-3 text-sm">
                <p className="text-yellow-800">
                  ⚠️ This ingredient has reached low quantity{' '}
                  <strong>{ingredient.quantityDetails.timesReachedLow}</strong> time(s)
                </p>
              </div>
            )}

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={updateIngredientMutation.isPending}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={updateIngredientMutation.isPending}>
                {updateIngredientMutation.isPending && (
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