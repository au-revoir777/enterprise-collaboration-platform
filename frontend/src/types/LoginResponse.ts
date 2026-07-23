import type { UserResponse } from "./UserResponse";

export interface LoginResponse{
    token: string;
    userResponse: UserResponse;
}