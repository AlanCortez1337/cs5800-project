'use client';

import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { useChartData, useTopEntities } from '@/hooks/useReports';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { ArrowLeft, Calendar } from 'lucide-react';
import { ReportType } from '@/lib/types';

const reportTitles: Record<string, string> = {
  recipe_used: 'Recipe Usage',
  ingredient_used: 'Ingredient Usage',
  times_ingredient_reached_low: 'Low Stock Alerts',
  recipes_created: 'Recipes Created',
  ingredients_created: 'Ingredients Created',
};

export default function ReportDetailPage() {
  const params = useParams();
  const router = useRouter();
  const type = (params.type as string).toUpperCase() as ReportType;
  
  const [groupBy, setGroupBy] = useState<'day' | 'week' | 'month'>('day');
  const [dateRange] = useState({
    start: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString(),
    end: new Date().toISOString(),
  });

  const { data: chartData, isLoading: chartLoading } = useChartData(
    type,
    groupBy,
    dateRange.start,
    dateRange.end
  );

  const { data: topEntities, isLoading: topLoading } = useTopEntities(
    type,
    10,
    dateRange.start,
    dateRange.end
  );

  const title = reportTitles[params.type as string] || 'Report Details';

  if (chartLoading || topLoading) {
    return (
      <div className="container mx-auto p-6">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
            <p className="mt-4 text-gray-600">Loading chart data...</p>
          </div>
        </div>
      </div>
    );
  }

  const totalCount = Array.isArray(chartData) ? chartData.reduce((sum, item) => sum + item.count, 0) : 0;
  const averagePerDay = Array.isArray(chartData) && chartData.length > 0 ? (totalCount / chartData.length).toFixed(1) : '0';

  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" onClick={() => router.push('/reports')}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div className="flex-1">
          <h1 className="text-3xl font-bold">{title}</h1>
          <p className="text-gray-600 mt-1">Detailed analytics and trends</p>
        </div>
        <div className="flex items-center gap-2">
          <Calendar className="h-4 w-4 text-gray-500" />
          <span className="text-sm text-gray-600">Last 30 days</span>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardDescription>Total Count</CardDescription>
            <CardTitle className="text-3xl">{totalCount}</CardTitle>
          </CardHeader>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardDescription>Average per Day</CardDescription>
            <CardTitle className="text-3xl">{averagePerDay}</CardTitle>
          </CardHeader>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardDescription>Peak Day</CardDescription>
            <CardTitle className="text-3xl">
              {Array.isArray(chartData) && chartData.length > 0
                ? chartData.reduce((max, item) => item.count > max.count ? item : max, chartData[0])?.count || 0
                : 0}
            </CardTitle>
          </CardHeader>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div>
              <CardTitle>Trend Over Time</CardTitle>
              <CardDescription>Track changes in {title.toLowerCase()}</CardDescription>
            </div>
            <Select value={groupBy} onValueChange={(value) => setGroupBy(value as any)}>
              <SelectTrigger className="w-[120px]">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="day">Daily</SelectItem>
                <SelectItem value="week">Weekly</SelectItem>
                <SelectItem value="month">Monthly</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardHeader>
        <CardContent>
          <ResponsiveContainer width="100%" height={350}>
            <LineChart data={chartData || []}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis 
                dataKey="date" 
                tick={{ fontSize: 12 }}
                angle={-45}
                textAnchor="end"
                height={80}
              />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="count" 
                stroke="#8884d8" 
                strokeWidth={2}
                name="Count"
              />
            </LineChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Top 10 by Count</CardTitle>
            <CardDescription>
              Most {type.includes('RECIPE') ? 'recipes' : 'ingredients'} in this category
            </CardDescription>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={350}>
              <BarChart data={topEntities || []}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="name" 
                  tick={{ fontSize: 12 }}
                  angle={-45}
                  textAnchor="end"
                  height={100}
                />
                <YAxis />
                <Tooltip />
                <Bar dataKey="count" fill="#82ca9d" name="Count" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Detailed List</CardTitle>
            <CardDescription>All items ranked by usage</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-2 max-h-[350px] overflow-y-auto">
              {Array.isArray(topEntities) && topEntities.length > 0 ? (
                topEntities.map((entity, index) => (
                  <div 
                    key={entity.id}
                    className="flex items-center justify-between p-3 border rounded-lg hover:bg-gray-50"
                  >
                    <div className="flex items-center gap-3">
                      <span className="text-lg font-bold text-gray-400">#{index + 1}</span>
                      <span className="font-medium">{entity.name}</span>
                    </div>
                    <span className="text-lg font-bold text-blue-600">{entity.count}</span>
                  </div>
                ))
              ) : (
                <p className="text-center text-gray-500 py-8">No data available</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}