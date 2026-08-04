import { create } from "zustand";
import { persist } from "zustand/middleware";

import type { UserResponse } from "../types/UserResponse";

interface AuthState {
    token: string | null;
    user: UserResponse | null;
    isAuthenticated: boolean;
    setToken: (token: string | null) => void;
    setUser: (user: UserResponse | null) => void;
    logout: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
    token: null,
    user: null,
    isAuthenticated: false,
    setToken: (token) =>
    set({
        token,
        isAuthenticated: token !== null,
    }),
    setUser: (user) =>
    set({
        user,
    }),
    logout: () =>
    set({
        token: null,
        user: null,
        isAuthenticated: false,
    }),
}),
{
    name: "auth-storage",
}
));