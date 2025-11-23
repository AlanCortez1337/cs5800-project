'use client';

import { useState } from 'react';
import { useIngredients, useDeleteIngredient } from '@/hooks/useIngredients';
import { DataTable } from '@/components/data-table';
import { getIngredientColumns } from '@/components/columns';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import { Ingredient } from '@/lib/types';
import { CreateIngredientDialog } from '@/components/CreateIngredientsDialog';
import { EditIngredientDialog } from '@/components/EditIngredientsDialog';
import { toast } from 'sonner';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';

export default function IngredientsTable() {
  const [selectedIngredient, setSelectedIngredient] = useState<Ingredient | null>(null);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [ingredientToDelete, setIngredientToDelete] = useState<number | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  
  const { data: ingredients, isLoading, error } = useIngredients();
  
  const deleteIngredientMutation = useDeleteIngredient({
    onSuccess: () => {
      toast.success('Ingredient deleted successfully!');
      setDeleteDialogOpen(false);
      setIngredientToDelete(null);
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
    setIngredientToDelete(id);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (ingredientToDelete) {
      deleteIngredientMutation.mutate(ingredientToDelete);
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

  const columns = getIngredientColumns({ onEdit: handleEdit, onDelete: handleDelete });

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
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This action cannot be undone. This will permanently delete the user
              and remove their data from the system.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDelete}
              className="bg-red-600 hover:bg-red-700"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}