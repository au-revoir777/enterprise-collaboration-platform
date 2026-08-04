import api from "../api/axios";

import type { PresenceResponse } from "../types/PresenceResponse";

class PresenceService {

    async getPresences(): Promise<PresenceResponse[]> {

        const response = await api.get<PresenceResponse[]>(
            "/users/presences",
        );

        return response.data;
    }

}

export default new PresenceService();