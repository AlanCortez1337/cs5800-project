import { NextRequest, NextResponse } from 'next/server';

const SPRING_BOOT_API = process.env.SPRING_BOOT_API_URL || 'http://localhost:8080';

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const type = searchParams.get('type');
  const start = searchParams.get('start');
  const end = searchParams.get('end');

  let url = `${SPRING_BOOT_API}/api/reports`;
  
  if (type) {
    url = `${SPRING_BOOT_API}/api/reports/type/${type}`;
  } else if (start && end) {
    url = `${SPRING_BOOT_API}/api/reports/range?start=${start}&end=${end}`;
  }

  try {
    const response = await fetch(url);
    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json({ error: 'Failed to fetch reports' }, { status: 500 });
  }
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const response = await fetch(`${SPRING_BOOT_API}/api/reports`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });
    const data = await response.json();
    return NextResponse.json(data, { status: 201 });
  } catch (error) {
    return NextResponse.json({ error: 'Failed to create report' }, { status: 500 });
  }
}