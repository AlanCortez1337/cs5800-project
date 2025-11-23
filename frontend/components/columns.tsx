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
    accessorKey: "id",
    header: "ID",
    cell: ({ row }) => <div className="font-medium">{row.getValue("id")}</div>,
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
    accessorKey: "unitDetails.unit",
    header: "Unit",
    cell: ({ row }) => {
      const ingredient = row.original;
      return (
        <div>
          <div>{ingredient.unitDetails.unit}</div>
          {ingredient.unitDetails.unitsPerContainer && (
            <div className="text-xs text-muted-foreground">
              {ingredient.unitDetails.unitsPerContainer} per container
            </div>
          )}
        </div>
      );
    },
  },
  {
    accessorKey: "quantityDetails.totalQuantity",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Total Qty
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      )
    },
    cell: ({ row }) => {
      const total = row.original.quantityDetails.totalQuantity;
      return <div className="text-center font-medium">{total}</div>;
    },
  },
  {
    accessorKey: "quantityDetails.availableQuantity",
    header: ({ column }) => {
      return (
        <Button
          variant="ghost"
          onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
        >
          Available
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      )
    },
    cell: ({ row }) => {
      const available = row.original.quantityDetails.availableQuantity;
      const total = row.original.quantityDetails.totalQuantity;
      const percentage = (available / total) * 100;
      
      let variant: "default" | "secondary" | "destructive" | "outline" = "default";
      if (percentage > 50) variant = "default";
      else if (percentage > 20) variant = "secondary";
      else variant = "destructive";

      return (
        <div className="text-center">
          <Badge variant={variant}>{available}</Badge>
        </div>
      );
    },
  },
  {
    accessorKey: "quantityDetails.reservedQuantity",
    header: "Reserved",
    cell: ({ row }) => {
      const reserved = row.original.quantityDetails.reservedQuantity ?? 0;
      return <div className="text-center text-muted-foreground">{reserved}</div>;
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
              onClick={() => navigator.clipboard.writeText(ingredient.id.toString())}
            >
              Copy ingredient ID
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={() => onEdit(ingredient)}>
              <Pencil className="mr-2 h-4 w-4" />
              Edit
            </DropdownMenuItem>
            <DropdownMenuItem 
              onClick={() => onDelete(ingredient.id)}
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