export interface RegisterRequest {
    username: string;
    displayName: string;
    email: string;
    password: string;
    confirmPassword: string;
    profilePictureUrl?: string;
}