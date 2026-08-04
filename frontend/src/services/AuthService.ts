import api from "../api/axios";

import type { RegisterRequest } from "../types/RegisterRequest";
import type { UserResponse } from "../types/UserResponse";
import type { LoginRequest } from "../types/LoginRequest";
import type { LoginResponse } from "../types/LoginResponse";
import { useAuthStore } from "../stores/authStore";

class AuthService {
    async register(request: RegisterRequest): Promise<UserResponse> {
        const response = await api.post<UserResponse>("/auth/register",request);
        return response.data;
    }

    async login(request: LoginRequest): Promise<LoginResponse> {
        const response = await api.post<LoginResponse>("/auth/login",request);

        const authStore = useAuthStore.getState();

        authStore.setToken(response.data.token);
        authStore.setUser(response.data.userResponse);
        return response.data;
    }

    async getCurrentUser(): Promise<UserResponse> {
        const response = await api.get<UserResponse>("/users/me");
        return response.data;
    }

    logout(): void {
        useAuthStore.getState().logout();
    }
}

export default new AuthService();