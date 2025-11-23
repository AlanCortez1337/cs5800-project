"use client"

import { ColumnDef } from "@tanstack/react-table"
import { Button } from "@/components/ui/button"
import { ArrowUpDown, MoreHorizontal, Pencil, Trash2 } from "lucide-react"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Badge } from "@/components/ui/badge"
import { Ingredient } from "@/lib/types"

interface ColumnsProps {
  onEdit: (ingredient: Ingredient) => void;
  onDelete: (id: number) => void;
}

export const getColumns = ({ onEdit, onDelete }: ColumnsProps): ColumnDef<Ingredient>[] => [
  {
    accessorKey: "ingredientID",
    header: "ID",
    cell: ({ row }) => <div className="font-medium">{row.getValue("ingredientID")}</div>, 
  },
  {
    accessorKey: "productName",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Product Name
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      )
    },
    cell: ({ row }) => <div className="font-semibold">{row.getValue("productName")}</div>,
  },
  {
    accessorKey: "unitDetails.unitOfMeasurement",  // Changed
    header: "Unit",
    cell: ({ row }) => {
      const ingredient = row.original;
      return (
        <div>
          <div>{ingredient.unitDetails.unitOfMeasurement}</div>
          {ingredient.unitDetails.pricePerUnit && (
            <div className="text-xs text-muted-foreground">
              ${ingredient.unitDetails.pricePerUnit.toFixed(2)} per unit
            </div>
          )}
        </div>
      );
    },
  },
  {
    accessorKey: "quantityDetails.currentQuantity",  // Changed
    header: "Current Qty",
    cell: ({ row }) => {
      const current = row.original.quantityDetails.currentQuantity;
      return <div className="text-center font-medium">{current}</div>;
    },
  },
  {
    accessorKey: "quantityDetails.maxQuantityLimit",  // Changed
    header: "Max Limit",
    cell: ({ row }) => {
      const max = row.original.quantityDetails.maxQuantityLimit;
      return <div className="text-center">{max ?? '-'}</div>;
    },
  },
  {
    accessorKey: "quantityDetails.alertLowQuantity",  // Changed
    header: "Low Alert",
    cell: ({ row }) => {
      const alert = row.original.quantityDetails.alertLowQuantity;
      const current = row.original.quantityDetails.currentQuantity;
      
      if (!alert) return <div className="text-center">-</div>;
      
      const isLow = current <= alert;
      return (
        <div className="text-center">
          <Badge variant={isLow ? "destructive" : "secondary"}>{alert}</Badge>
        </div>
      );
    },
  },
  {
    id: "actions",
    cell: ({ row }) => {
      const ingredient = row.original;
      return (
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="h-8 w-8 p-0">
            <span className="sr-only">Open menu</span>
            <MoreHorizontal className="h-4 w-4" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuLabel>Actions</DropdownMenuLabel>
          <DropdownMenuItem
            onClick={() => navigator.clipboard.writeText(ingredient.ingredientID.toString())}
          >
            Copy ingredient ID
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem onClick={() => onEdit(ingredient)}>
            <Pencil className="mr-2 h-4 w-4" />
            Edit
          </DropdownMenuItem>
          <DropdownMenuItem 
            onClick={() => onDelete(ingredient.ingredientID)}

interface UserColumnsProps {
  onEdit: (user: User) => void;
  onDelete: (id: number) => void;
}

export const getUserColumns = ({ onEdit, onDelete }: UserColumnsProps): ColumnDef<User>[] => [
  {
    accessorKey: "id",
    header: "ID",
    cell: ({ row }) => <div className="font-medium">{row.getValue("id")}</div>,
  },
  {
    accessorKey: "username",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Username
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      )
    },
    cell: ({ row }) => <div className="font-semibold">{row.getValue("username")}</div>,
  },
  {
    accessorKey: "role",
    header: "Role",
    cell: ({ row }) => {
      const role = row.getValue("role") as string;
      return (
        <Badge variant={role === "ADMIN" ? "default" : "secondary"}>
          {role}
        </Badge>
      );
    },
  },
  {
    id: "actions",
    cell: ({ row }) => {
      const user = row.original;

      return (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-8 w-8 p-0">
              <span className="sr-only">Open menu</span>
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>Actions</DropdownMenuLabel>
            <DropdownMenuItem
              onClick={() => navigator.clipboard.writeText(user.id.toString())}
            >
              Copy user ID
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={() => onEdit(user)}>
              <Pencil className="mr-2 h-4 w-4" />
              Edit
            </DropdownMenuItem>
            <DropdownMenuItem 
              onClick={() => onDelete(user.id)}
              className="text-red-600"
            >
              <Trash2 className="mr-2 h-4 w-4" />
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
  },
];