import type { Role } from "./Role";
import type { UserStatus } from "./UserStatus";

export interface UserResponse {
    username: string;
    displayName: string;
    email: string;
    role: Role;
    status: UserStatus;
    profilePictureUrl?: string;
}