'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useReportSummary } from '@/hooks/useReports';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { BarChart3, TrendingUp, Package, ChefHat, AlertTriangle } from 'lucide-react';

const reportTypeConfig = {
  RECIPE_USED: {
    title: 'Recipe Usage',
    description: 'Track how often recipes are used',
    icon: ChefHat,
    color: 'text-blue-500',
    bgColor: 'bg-blue-50',
  },
  INGREDIENT_USED: {
    title: 'Ingredient Usage',
    description: 'Monitor ingredient consumption',
    icon: Package,
    color: 'text-green-500',
    bgColor: 'bg-green-50',
  },
  TIMES_INGREDIENT_REACHED_LOW: {
    title: 'Low Stock Alerts',
    description: 'Ingredients that reached low levels',
    icon: AlertTriangle,
    color: 'text-red-500',
    bgColor: 'bg-red-50',
  },
  RECIPES_CREATED: {
    title: 'Recipes Created',
    description: 'New recipes added to the system',
    icon: TrendingUp,
    color: 'text-purple-500',
    bgColor: 'bg-purple-50',
  },
  INGREDIENTS_CREATED: {
    title: 'Ingredients Created',
    description: 'New ingredients added to inventory',
    icon: Package,
    color: 'text-orange-500',
    bgColor: 'bg-orange-50',
  },
};

export default function ReportsPage() {
  const [dateRange, setDateRange] = useState({
    start: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString(),
    end: new Date().toISOString(),
  });

  const { data: summary, isLoading } = useReportSummary(dateRange.start, dateRange.end);

  if (isLoading) {
    return (
      <div className="container mx-auto p-6">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
            <p className="mt-4 text-gray-600">Loading reports...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Reports & Analytics</h1>
          <p className="text-gray-600 mt-1">
            View insights and trends for your recipes and ingredients
          </p>
        </div>
        <Button variant="outline">
          <BarChart3 className="mr-2 h-4 w-4" />
          Export Data
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {Object.entries(reportTypeConfig).map(([type, config]) => {
          const Icon = config.icon;
          const count = summary?.[type] || 0;

          return (
            <Link key={type} href={`/dashboard/reports/${type.toLowerCase()}`}>
              <Card className="hover:shadow-lg transition-shadow cursor-pointer">
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <div className={`p-3 rounded-lg ${config.bgColor}`}>
                      <Icon className={`h-6 w-6 ${config.color}`} />
                    </div>
                    <div className="text-right">
                      <p className="text-3xl font-bold">{count}</p>
                      <p className="text-xs text-gray-500">Last 30 days</p>
                    </div>
                  </div>
                  <CardTitle className="mt-4">{config.title}</CardTitle>
                  <CardDescription>{config.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <Button variant="ghost" className="w-full">
                    View Details →
                  </Button>
                </CardContent>
              </Card>
            </Link>
          );
        })}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Quick Stats</CardTitle>
          <CardDescription>Summary of activity in the last 30 days</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
            {Object.entries(reportTypeConfig).map(([type, config]) => (
              <div key={type} className="text-center p-4 border rounded-lg">
                <p className="text-sm text-gray-600">{config.title}</p>
                <p className="text-2xl font-bold mt-1">{summary?.[type] || 0}</p>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}