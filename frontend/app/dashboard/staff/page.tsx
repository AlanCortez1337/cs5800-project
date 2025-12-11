import { ProtectedRoute } from "@/components/ProtectedRoute";
import UsersTable from "@/components/UsersTable";

export default function StaffPage() {
    return <ProtectedRoute><UsersTable /></ProtectedRoute>
}