import { NextRequest, NextResponse } from 'next/server';
const SPRING_BOOT_API = process.env.SPRING_BOOT_API_URL || 'http://localhost:8080';

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const start = searchParams.get('start');
  const end = searchParams.get('end');

  const params = new URLSearchParams();
  if (start) params.append('start', start);
  if (end) params.append('end', end);

  try {
    const response = await fetch(`${SPRING_BOOT_API}/api/reports/dashboard?${params}`);
    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json({ error: 'Failed to fetch dashboard' }, { status: 500 });
  }
}