import { NextRequest, NextResponse } from 'next/server';

const SPRING_BOOT_API = process.env.SPRING_BOOT_API_URL || 'http://localhost:8080';

export async function POST(
  request: NextRequest
) {
  try {
    const response = await fetch(`${SPRING_BOOT_API}/api/auth/logout`, {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        Cookie: request.headers.get('cookie') || '',
    },
    });

    if (!response.ok) {
    return NextResponse.json(
        { success: false, error: 'Logout failed' },
        { status: response.status }
    );
    }

    const data = await response.json();

    // Forward set-cookie headers (likely clearing the session)
    const nextResponse = NextResponse.json(data);
    const setCookie = response.headers.get('set-cookie');
    if (setCookie) {
    nextResponse.headers.set('set-cookie', setCookie);
    }

    return nextResponse;
} catch (error) {
    console.error('Logout failed:', error);
    return NextResponse.json(
    { success: false, error: 'Logout failed' },
    { status: 500 }
    );
}
}