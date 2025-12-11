import { ProtectedRoute } from "@/components/ProtectedRoute";
import ReportDetailPage from "./reportTypePage";

export default function ReportsType() {
  return (
    <ProtectedRoute>
      <ReportDetailPage />
    </ProtectedRoute>
  )
}