import type { UserStatus } from "./UserStatus";

export interface PresenceResponse {
    userId: number;
    username: string;
    status: UserStatus;
}
