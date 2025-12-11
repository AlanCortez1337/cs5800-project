import { NextResponse } from 'next/server';
const SPRING_BOOT_API = process.env.SPRING_BOOT_API_URL || 'http://localhost:8080';

export async function POST() {
  try {
    const response = await fetch(`${SPRING_BOOT_API}/api/seed/users`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Spring Boot API responded with status ${response.status}: ${errorText}`);
    }

    const data = await response.text();
    return NextResponse.json({ message: data });
  } catch (error) {
    return NextResponse.json({ error: 'Failed to seed data' }, { status: 500 });
  }
}