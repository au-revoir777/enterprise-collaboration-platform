import axios from "../api/axios";

import type { RegisterRequest } from "../types/RegisterRequest";
import type { UserResponse } from "../types/UserResponse";
import type { LoginRequest } from "../types/LoginRequest";
import type { LoginResponse } from "../types/LoginResponse";

class AuthService {
    async register(request: RegisterRequest): Promise<UserResponse> {
        const response = await axios.post<UserResponse>("/auth/register",request);
        return response.data;
    }

    async login(request: LoginRequest): Promise<LoginResponse> {
        const response = await axios.post<LoginResponse>("/auth/login",request);
        return response.data;
    }
}

export default new AuthService();