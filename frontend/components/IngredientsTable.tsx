'use client';

import { useState } from 'react';
import { useIngredients, useDeleteIngredient } from '@/hooks/useIngredients';
import { DataTable } from '@/components/data-table';
import { getColumns } from '@/components/columns';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import { Ingredient } from '@/lib/types';
import { CreateIngredientDialog } from '@/components/CreateIngredientsDialog';
import { EditIngredientDialog } from '@/components/EditIngredientsDialog';
import { toast } from 'sonner';

export default function IngredientsTable() {
  const [selectedIngredient, setSelectedIngredient] = useState<Ingredient | null>(null);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  
  const { data: ingredients, isLoading, error } = useIngredients();
  
  const deleteIngredientMutation = useDeleteIngredient({
    onSuccess: () => {
      toast.success('Ingredient deleted successfully!');
    },
    onError: (error) => {
      toast.error(`Failed to update ingredient: ${error.message}`);
    },
  });

  const handleEdit = (ingredient: Ingredient) => {
    setSelectedIngredient(ingredient);
    setEditDialogOpen(true);
  };

  const handleDelete = (id: number) => {
    if (confirm('Are you sure you want to delete this ingredient?')) {
      deleteIngredientMutation.mutate(id);
    }
  };

  const handleCreate = () => {
    setCreateDialogOpen(true);
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
    <>
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

      <CreateIngredientDialog
        open={createDialogOpen}
        onOpenChange={setCreateDialogOpen}
      />
      
      <EditIngredientDialog
        open={editDialogOpen}
        onOpenChange={setEditDialogOpen}
        ingredient={selectedIngredient}
      />
    </>
  );
}