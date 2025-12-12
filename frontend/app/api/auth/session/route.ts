import { NextRequest, NextResponse } from 'next/server';

const SPRING_BOOT_API = process.env.SPRING_BOOT_API_URL || 'http://localhost:8080';

export async function GET(
  request: NextRequest,
) {

  try {
    const response = await fetch(`${SPRING_BOOT_API}/api/auth/session`, {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json',
        // Forward cookies from the client
        Cookie: request.headers.get('cookie') || '',
    },
    });

    if (!response.ok) {
    return NextResponse.json(
        { authenticated: false },
        { status: response.status }
    );
    }

    const data = await response.json();

    // Forward set-cookie headers from Spring Boot to client
    const nextResponse = NextResponse.json(data);
    const setCookies = response.headers.getSetCookie();
    setCookies.forEach(cookie => {
        nextResponse.headers.append('set-cookie', cookie);
    });

    return nextResponse;
} catch (error) {
    console.error('Session check failed:', error);
    return NextResponse.json(
    { authenticated: false },
    { status: 500 }
    );
}
}