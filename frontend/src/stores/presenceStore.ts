import { create } from "zustand";
import type { PresenceResponse } from "../types/PresenceResponse";

interface PresenceState {
    presence: Map<string, PresenceResponse>;
    initialize: (users: PresenceResponse[]) => void;
    updatePresence: (presence: PresenceResponse) => void;
    clear: () => void;
}

export const usePresenceStore = create<PresenceState>((set) => ({

    presence: new Map(),

    initialize: (users) =>
        set({
            presence: new Map(
                users.map((user) => [
                    user.username,
                    user,
                ]),
            ),
        }),

    updatePresence: (presence) =>
        set((state) => {

            const updated = new Map(state.presence);

            updated.set(
                presence.username,
                presence,
            );

            return {
                presence: updated,
            };
        }),

    clear: () =>
        set({
            presence: new Map(),
        }),

}));