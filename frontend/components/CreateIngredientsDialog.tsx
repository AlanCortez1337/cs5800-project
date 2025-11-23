'use client';

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
import { useCreateIngredient } from '@/hooks/useIngredients';
import { Loader2 } from 'lucide-react';
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

interface CreateIngredientDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function CreateIngredientDialog({
  open,
  onOpenChange,
}: CreateIngredientDialogProps) {
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

    const createIngredientMutation = useCreateIngredient({
        onSuccess: () => {
            form.reset();
            onOpenChange(false);
            toast.success('Ingredient created successfully!');
        },
        onError: (error) => {
            toast.error(`Failed to create ingredient: ${error.message}`);
        },
    });

  function onSubmit(values: FormValues) {
    createIngredientMutation.mutate({
      productName: values.productName,
      unitDetails: {
        unitOfMeasurement: values.unitOfMeasurement,
        pricePerUnit: values.pricePerUnit,
      },
      quantityDetails: {
        currentQuantity: values.currentQuantity,
        maxQuantityLimit: values.maxQuantityLimit,
        alertLowQuantity: values.alertLowQuantity,
        timesReachedLow: 0,
      },
    });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[525px]">
        <DialogHeader>
          <DialogTitle>Create New Ingredient</DialogTitle>
          <DialogDescription>
            Add a new ingredient to your inventory. Click save when {"you're"} done.
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

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={createIngredientMutation.isPending}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={createIngredientMutation.isPending}>
                {createIngredientMutation.isPending && (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                )}
                Create Ingredient
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}