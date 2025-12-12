

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { AuthContextType, User } from '../types';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    // Check session on mount
    useEffect(() => {
        checkSession();
    }, []);

    const checkSession = async () => {
        try {
            const response = await fetch(`/api/auth/session`, {
                credentials: 'include',
            });
            
            if (!response.ok) {
            // If response is not ok, user is not authenticated
            setUser(null);
            return;
            }
            
            const data = await response.json();
            
            if (data.authenticated) {
            setUser(data.user);
            } else {
            setUser(null);
            }
        } catch (error) {
            console.error('Session check failed:', error);
            setUser(null); // Set to null on error
        } finally {
            setLoading(false);
        }
    };

    const login = async (userName: string, password: string): Promise<boolean> => {
        try {
            const response = await fetch(`/api/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({ userName, password }),
            });
            
            const data = await response.json();
            
            if (data.success) {
                setUser(data.user);
                return true;
            }
            return false;
        } catch (error) {
            console.error('Login failed:', error);
            return false;
        }
        };

    const logout = async () => {
        try {
            await fetch(`/api/auth/logout`, {
                method: 'POST',
                credentials: 'include',
            });
            setUser(null);
        } catch (error) {
            console.error('Logout failed:', error);
        }
    };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}