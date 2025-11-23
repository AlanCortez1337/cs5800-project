import { NextRequest, NextResponse } from 'next/server';

const SPRING_BOOT_API = process.env.SPRING_BOOT_API_URL || 'http://localhost:8080';

// GET all ingredients or ingredient by ID
export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const id = searchParams.get('id');

  try {
    let endpoint = `${SPRING_BOOT_API}/api/ingredients`;

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
        { error: 'Ingredient not found' },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to fetch ingredient data' },
      { status: 500 }
    );
  }
}

// POST create new ingredient
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();

    const response = await fetch(`${SPRING_BOOT_API}/api/ingredients`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Failed to create ingredient' },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data, { status: 201 });
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to create ingredient' },
      { status: 500 }
    );
  }
}

// PUT update ingredient
export async function PUT(request: NextRequest) {
  try {
    const body = await request.json();
    const { ingredientID, ...ingredientDetails } = body;

    if (!ingredientID) {
      return NextResponse.json(
        { error: 'Ingredient ID is required' },
        { status: 400 }
      );
    }

    const response = await fetch(`${SPRING_BOOT_API}/api/ingredients/${ingredientID}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(ingredientDetails),
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Failed to update ingredient' },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to update ingredient' },
      { status: 500 }
    );
  }
}

// DELETE ingredient
export async function DELETE(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const id = searchParams.get('id');

  if (!id) {
    return NextResponse.json(
      { error: 'Ingredient ID is required' },
      { status: 400 }
    );
  }

  try {
    const response = await fetch(`${SPRING_BOOT_API}/api/ingredients/${id}`, {
      method: 'DELETE',
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Failed to delete ingredient' },
        { status: response.status }
      );
    }

    return NextResponse.json({ message: 'Ingredient deleted successfully' });
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to delete ingredient' },
      { status: 500 }
    );
  }
}