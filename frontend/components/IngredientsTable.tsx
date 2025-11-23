'use client';

import { useState } from 'react';
import { useIngredients, useDeleteIngredient } from '@/hooks/useIngredients';
import { DataTable } from '@/components/data-table';
import { getColumns } from '@/components/columns';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import { Ingredient } from '@/lib/types';

export default function IngredientsTable() {
  const [selectedIngredient, setSelectedIngredient] = useState<Ingredient | null>(null);
  
  const { data: ingredients, isLoading, error } = useIngredients();
  
  const deleteIngredientMutation = useDeleteIngredient({
    onSuccess: () => {
      alert('Ingredient deleted successfully!');
    },
    onError: (error) => {
      alert(`Error: ${error.message}`);
    },
  });

  const handleEdit = (ingredient: Ingredient) => {
    setSelectedIngredient(ingredient);
    // Open your edit modal/form here
    console.log('Edit ingredient:', ingredient);
  };

  const handleDelete = (id: number) => {
    if (confirm('Are you sure you want to delete this ingredient?')) {
      deleteIngredientMutation.mutate(id);
    }
  };

  const handleCreate = () => {
    // Open your create modal/form here
    console.log('Create new ingredient');
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg">Loading ingredients...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-red-500">Error: {error.message}</div>
      </div>
    );
  }

  const columns = getColumns({ onEdit: handleEdit, onDelete: handleDelete });

  return (
    <div className="container mx-auto py-10">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Ingredients</h1>
          <p className="text-muted-foreground">Manage your ingredient inventory</p>
        </div>
        <Button onClick={handleCreate}>
          <Plus className="mr-2 h-4 w-4" />
          Add Ingredient
        </Button>
      </div>
      
      <DataTable columns={columns} data={ingredients || []} />
    </div>
  );
}