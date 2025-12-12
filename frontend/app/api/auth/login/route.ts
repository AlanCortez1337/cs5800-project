import { NextRequest, NextResponse } from 'next/server';

const SPRING_BOOT_API = process.env.SPRING_BOOT_API_URL || 'http://localhost:8080';

export async function POST(request: NextRequest) {
    const body = await request.json();

    const response = await fetch(`${SPRING_BOOT_API}/api/auth/login`, {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json',
        Cookie: request.headers.get('cookie') || '',
        },
        body: JSON.stringify(body),
    });

    const data = await response.json();

    const nextResponse = NextResponse.json(data, { status: response.status });

    const setCookies = response.headers.getSetCookie();
    setCookies.forEach(cookie => {
        nextResponse.headers.append('set-cookie', cookie);
    });

  return nextResponse;
}