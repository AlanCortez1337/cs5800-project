import { CreateUserInput, UpdateUserInput, User } from "@/lib/types";

// GET all users
export const getAllUsers = async (): Promise<User[]> => {
  const response = await fetch('/api/user');
  if (!response.ok) throw new Error('Failed to fetch users');
  return response.json();
};

// GET user by ID
export const getUserById = async (id: number): Promise<User> => {
  const response = await fetch(`/api/user?id=${id}`);  // FIXED
  if (!response.ok) throw new Error('Failed to fetch user');
  return response.json();
};

// GET user by username
export const getUserByUsername = async (userName: string): Promise<User> => {
  const response = await fetch(`/api/user?userName=${userName}`);  // FIXED
  if (!response.ok) throw new Error('Failed to fetch user');
  return response.json();
};

// GET user by staff ID
export const getUserByStaffId = async (staffId: number): Promise<User> => {
  const response = await fetch(`/api/user?staffId=${staffId}`);  // FIXED
  if (!response.ok) throw new Error('Failed to fetch staff user');
  return response.json();
};

// GET user by admin ID
export const getUserByAdminId = async (adminId: number): Promise<User> => {
  const response = await fetch(`/api/user?adminId=${adminId}`);  // FIXED
  if (!response.ok) throw new Error('Failed to fetch admin user');
  return response.json();
};

// POST create user
export const createUser = async (data: CreateUserInput): Promise<User> => {
  const response = await fetch('/api/user', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to create user');
  return response.json();
};

// PUT update user
export const updateUser = async (data: UpdateUserInput): Promise<User> => {
  const response = await fetch('/api/user', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to update user');
  return response.json();
};

// DELETE user
export const deleteUser = async (id: number): Promise<void> => {
  const response = await fetch(`/api/user?id=${id}`, {  // FIXED
    method: 'DELETE',
  });
  if (!response.ok) throw new Error('Failed to delete user');
};