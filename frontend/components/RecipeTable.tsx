'use client';

import { useState } from 'react';
import { useRecipes, useDeleteRecipe } from '@/hooks/useRecipes';
import { DataTable } from '@/components/data-table';
import { getRecipeColumns } from '@/components/columns';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';
import { Recipe } from '@/lib/types';
import { CreateRecipeDialog } from '@/components/CreateRecipeDialog';
import { EditRecipeDialog } from '@/components/EditRecipeDialog';
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

export default function RecipesTable() {
  const [selectedRecipe, setSelectedRecipe] = useState<Recipe | null>(null);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [recipeToDelete, setRecipeToDelete] = useState<number | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  
  const { data: recipes, isLoading, error } = useRecipes();
  
  const deleteRecipeMutation = useDeleteRecipe({
    onSuccess: () => {
      toast.success('Recipe deleted successfully!');
      setDeleteDialogOpen(false);
      setRecipeToDelete(null);
    },
    onError: (error) => {
      toast.error(`Error: ${error.message}`);
    },
  });

  const handleEdit = (recipe: Recipe) => {
    setSelectedRecipe(recipe);
    setEditDialogOpen(true);
  };

  const handleDelete = (id: number) => {
    setRecipeToDelete(id);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (recipeToDelete) {
      deleteRecipeMutation.mutate(recipeToDelete);
    }
  };

  const handleCreate = () => {
    setCreateDialogOpen(true);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg">Loading recipes...</div>
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

  const columns = getRecipeColumns({ onEdit: handleEdit, onDelete: handleDelete });

  return (
    <>
      <div className="container mx-auto py-10">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold">Recipes</h1>
            <p className="text-muted-foreground">Manage your recipe collection</p>
          </div>
          <Button onClick={handleCreate}>
            <Plus className="mr-2 h-4 w-4" />
            Add Recipe
          </Button>
        </div>
        
        <DataTable columns={columns} data={recipes || []} />
      </div>

      <CreateRecipeDialog
        open={createDialogOpen}
        onOpenChange={setCreateDialogOpen}
      />
      
      <EditRecipeDialog
        open={editDialogOpen}
        onOpenChange={setEditDialogOpen}
        recipe={selectedRecipe}
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