import { User, AuthResponse, LoginRequest, RegisterRequest } from '@/types';
import { api } from './api';
import Cookies from 'js-cookie';

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

export class AuthService {
  static getToken(): string | null {
    return Cookies.get(TOKEN_KEY) || null;
  }

  static setToken(token: string): void {
    Cookies.set(TOKEN_KEY, token, { expires: 7, secure: true, sameSite: 'strict' });
  }

  static removeToken(): void {
    Cookies.remove(TOKEN_KEY);
  }

  static getUser(): User | null {
    const userStr = localStorage.getItem(USER_KEY);
    if (!userStr) return null;
    
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  }

  static setUser(user: User): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  static removeUser(): void {
    localStorage.removeItem(USER_KEY);
  }

  static isAuthenticated(): boolean {
    return !!this.getToken() && !!this.getUser();
  }

  static hasRole(role: string): boolean {
    const user = this.getUser();
    return user?.role === role;
  }

  static hasAnyRole(roles: string[]): boolean {
    const user = this.getUser();
    return user ? roles.includes(user.role) : false;
  }

  static isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  static isSupportAgent(): boolean {
    return this.hasAnyRole(['SUPPORT_AGENT', 'ADMIN']);
  }

  static async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    
    this.setToken(response.data.token);
    this.setUser(response.data.user);
    
    return response.data;
  }

  static async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/auth/register', userData);
    
    this.setToken(response.data.token);
    this.setUser(response.data.user);
    
    return response.data;
  }

  static async logout(): Promise<void> {
    this.removeToken();
    this.removeUser();
    
    // Redirect to login page
    window.location.href = '/login';
  }

  static async refreshToken(): Promise<AuthResponse | null> {
    try {
      const token = this.getToken();
      if (!token) return null;

      const response = await api.post<AuthResponse>('/auth/refresh', {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      this.setToken(response.data.token);
      this.setUser(response.data.user);
      
      return response.data;
    } catch {
      this.logout();
      return null;
    }
  }

  static async validateToken(): Promise<boolean> {
    try {
      const token = this.getToken();
      if (!token) return false;

      const response = await api.post<boolean>('/auth/validate', {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      return response.data;
    } catch {
      return false;
    }
  }

  static async getCurrentUser(): Promise<User | null> {
    try {
      const response = await api.get<User>('/users/profile');
      this.setUser(response.data);
      return response.data;
    } catch {
      return null;
    }
  }
}
