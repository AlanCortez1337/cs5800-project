import { ChartDataPoint, DashboardData, ReportType, TopEntity } from '@/lib/types';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
// Get all reports or filter by type
export const useReports = (type?: ReportType, start?: string, end?: string) => {
  return useQuery({
    queryKey: ['reports', type, start, end],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (type) params.append('type', type);
      if (start) params.append('start', start);
      if (end) params.append('end', end);

      const response = await fetch(`/api/reports?${params}`);
      if (!response.ok) throw new Error('Failed to fetch reports');
      return response.json() as Promise<Report[]>;
    },
  });
};

// Get summary data
export const useReportSummary = (start?: string, end?: string) => {
  return useQuery({
    queryKey: ['reports', 'summary', start, end],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (start) params.append('start', start);
      if (end) params.append('end', end);

      const response = await fetch(`/api/reports/summary?${params}`);
      if (!response.ok) throw new Error('Failed to fetch summary');
      return response.json() as Promise<Record<string, number>>;
    },
  });
};

// Get dashboard data
export const useDashboard = (start?: string, end?: string) => {
  return useQuery({
    queryKey: ['reports', 'dashboard', start, end],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (start) params.append('start', start);
      if (end) params.append('end', end);

      const response = await fetch(`/api/reports/dashboard?${params}`);
      if (!response.ok) throw new Error('Failed to fetch dashboard');
      return response.json() as Promise<DashboardData>;
    },
  });
};

// Get chart data
export const useChartData = (
  reportType: ReportType,
  groupBy: 'day' | 'week' | 'month' = 'day',
  start?: string,
  end?: string
) => {
  return useQuery({
    queryKey: ['reports', 'chart', reportType, groupBy, start, end],
    queryFn: async () => {
      const params = new URLSearchParams({
        reportType,
        groupBy,
      });
      if (start) params.append('start', start);
      if (end) params.append('end', end);

      const response = await fetch(`/api/reports/chart?${params}`);
      if (!response.ok) throw new Error('Failed to fetch chart data');
      return response.json() as Promise<ChartDataPoint[]>;
    },
    enabled: !!reportType,
  });
};

// Get top entities
export const useTopEntities = (
  reportType: ReportType,
  limit: number = 10,
  start?: string,
  end?: string
) => {
  return useQuery({
    queryKey: ['reports', 'top', reportType, limit, start, end],
    queryFn: async () => {
      const params = new URLSearchParams({
        reportType,
        limit: limit.toString(),
      });
      if (start) params.append('start', start);
      if (end) params.append('end', end);

      const response = await fetch(`/api/reports/top?${params}`);
      if (!response.ok) throw new Error('Failed to fetch top entities');
      return response.json() as Promise<TopEntity[]>;
    },
    enabled: !!reportType,
  });
};

// Create a report
export const useCreateReport = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: {
      reportType: ReportType;
      entityId: number;
      entityName: string;
    }) => {
      const response = await fetch('/api/reports', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });
      if (!response.ok) throw new Error('Failed to create report');
      return response.json() as Promise<Report>;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reports'] });
    },
  });
};