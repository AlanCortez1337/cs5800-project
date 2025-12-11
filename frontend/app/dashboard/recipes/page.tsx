import { ProtectedRoute } from "@/components/ProtectedRoute";
import RecipesTable from "@/components/RecipeTable";

export default function RecipesPage() {
    return <ProtectedRoute><RecipesTable /></ProtectedRoute>;
}