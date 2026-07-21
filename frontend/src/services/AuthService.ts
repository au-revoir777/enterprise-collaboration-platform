import axios from "../api/axios";

import type { RegisterRequest } from "../types/RegisterRequest";
import type { UserResponse } from "../types/UserResponse";

class AuthService {
    async register(request: RegisterRequest): Promise<UserResponse> {
        const response = await axios.post<UserResponse>("/auth/register",request);
        return response.data;
    }
}

export default new AuthService();