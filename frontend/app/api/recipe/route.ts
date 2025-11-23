import { NextRequest, NextResponse } from 'next/server';

const SPRING_BOOT_API = process.env.SPRING_BOOT_API_URL || 'http://localhost:8080';

// GET all recipes or recipe by ID
export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const id = searchParams.get('id');

  try {
    let endpoint = `${SPRING_BOOT_API}/api/recipes`;

    if (id) {
      endpoint += `/${id}`;
    }

    const response = await fetch(endpoint, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Recipe not found' },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to fetch recipe data' },
      { status: 500 }
    );
  }
}

// POST create new recipe
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();

    const response = await fetch(`${SPRING_BOOT_API}/api/recipes`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Failed to create recipe' },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data, { status: 201 });
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to create recipe' },
      { status: 500 }
    );
  }
}

// PUT update recipe
export async function PUT(request: NextRequest) {
  try {
    const body = await request.json();
    const { id, ...recipeDetails } = body;

    if (!id) {
      return NextResponse.json(
        { error: 'Recipe ID is required' },
        { status: 400 }
      );
    }

    const response = await fetch(`${SPRING_BOOT_API}/api/recipes/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(recipeDetails),
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Failed to update recipe' },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to update recipe' },
      { status: 500 }
    );
  }
}

// DELETE recipe
export async function DELETE(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const id = searchParams.get('id');

  if (!id) {
    return NextResponse.json(
      { error: 'Recipe ID is required' },
      { status: 400 }
    );
  }

  try {
    const response = await fetch(`${SPRING_BOOT_API}/api/recipes/${id}`, {
      method: 'DELETE',
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Failed to delete recipe' },
        { status: response.status }
      );
    }

    return NextResponse.json({ message: 'Recipe deleted successfully' });
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to delete recipe' },
      { status: 500 }
    );
  }
}