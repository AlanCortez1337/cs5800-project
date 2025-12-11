import { ProtectedRoute } from "@/components/ProtectedRoute";
import ReportsPage from "./reportsPage";

export default function Reports() {
  return (
    <ProtectedRoute>
      <ReportsPage />
    </ProtectedRoute>
  )
}