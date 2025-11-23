import { 
  useQuery, 
  useMutation, 
  useQueryClient,
  UseQueryOptions,
  UseMutationOptions 
} from '@tanstack/react-query';
import * as userApi from '@/lib/userAPI';
import { CreateUserInput, UpdateUserInput, User } from '@/lib/types';

// Query keys for cache management
export const userKeys = {
  all: ['users'] as const,
  lists: () => [...userKeys.all, 'list'] as const,
  list: (filters?: object) => [...userKeys.lists(), filters] as const,
  details: () => [...userKeys.all, 'detail'] as const,
  detail: (id: number) => [...userKeys.details(), id] as const,
  byUsername: (username: string) => [...userKeys.all, 'username', username] as const,
  byStaffId: (staffId: number) => [...userKeys.all, 'staff', staffId] as const,
  byAdminId: (adminId: number) => [...userKeys.all, 'admin', adminId] as const,
};

// GET all users
export const useUsers = (options?: Omit<UseQueryOptions<User[]>, 'queryKey' | 'queryFn'>) => {
  return useQuery({
    queryKey: userKeys.lists(),
    queryFn: userApi.getAllUsers,
    ...options,
  });
};

// GET user by ID
export const useUser = (
  id: number,
  options?: Omit<UseQueryOptions<User>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: userKeys.detail(id),
    queryFn: () => userApi.getUserById(id),
    enabled: !!id,
    ...options,
  });
};

// GET user by username
export const useUserByUsername = (
  userName: string,
  options?: Omit<UseQueryOptions<User>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: userKeys.byUsername(userName),
    queryFn: () => userApi.getUserByUsername(userName),
    enabled: !!userName,
    ...options,
  });
};

// GET user by staff ID
export const useStaffUser = (
  staffId: number,
  options?: Omit<UseQueryOptions<User>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: userKeys.byStaffId(staffId),
    queryFn: () => userApi.getUserByStaffId(staffId),
    enabled: !!staffId,
    ...options,
  });
};

// GET user by admin ID
export const useAdminUser = (
  adminId: number,
  options?: Omit<UseQueryOptions<User>, 'queryKey' | 'queryFn'>
) => {
  return useQuery({
    queryKey: userKeys.byAdminId(adminId),
    queryFn: () => userApi.getUserByAdminId(adminId),
    enabled: !!adminId,
    ...options,
  });
};

// POST create user
export const useCreateUser = (
  options?: UseMutationOptions<User, Error, CreateUserInput>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: userApi.createUser,
    onSuccess: (data) => {
      // Invalidate and refetch all user lists
      queryClient.invalidateQueries({ queryKey: userKeys.lists() });
      // Optionally set the new user in cache
      queryClient.setQueryData(userKeys.detail(data.id), data);
    },
    ...options,
  });
};

// PUT update user
export const useUpdateUser = (
  options?: UseMutationOptions<User, Error, UpdateUserInput>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: userApi.updateUser,
    onSuccess: (data, variables) => {
      // Update the specific user in cache
      queryClient.setQueryData(userKeys.detail(variables.id), data);
      // Invalidate all user lists to refetch
      queryClient.invalidateQueries({ queryKey: userKeys.lists() });
    },
    ...options,
  });
};

// DELETE user
export const useDeleteUser = (
  options?: UseMutationOptions<void, Error, number>
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: userApi.deleteUser,
    onSuccess: (_, deletedId) => {
      // Remove the user from cache
      queryClient.removeQueries({ queryKey: userKeys.detail(deletedId) });
      // Invalidate all user lists
      queryClient.invalidateQueries({ queryKey: userKeys.lists() });
    },
    ...options,
  });
};