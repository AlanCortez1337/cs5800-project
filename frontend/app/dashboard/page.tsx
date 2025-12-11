import IngredientsTable from "@/components/IngredientsTable";
import { ProtectedRoute } from "@/components/ProtectedRoute";

export default function Home() {
  return (
    <ProtectedRoute>
      <IngredientsTable />
    </ProtectedRoute>
  );
}
