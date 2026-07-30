import api from "../api/axios";

import type { ChannelResponse } from "../types/ChannelResponse";
import type { CreateChannelRequest } from "../types/CreateChannelRequest";

class ChannelService {

    async createChannel(
        workspaceId: number,
        request: CreateChannelRequest
    ): Promise<ChannelResponse> {

        const response = await api.post<ChannelResponse>(
            `/workspaces/${workspaceId}/channels`,
            request
        );

        return response.data;
    }

    async getWorkspaceChannels(
    workspaceId: number
    ): Promise<ChannelResponse[]> {

        const response = await api.get<ChannelResponse[]>(
            `/workspaces/${workspaceId}/channels`
        );

        return response.data;
    }   

}

export default new ChannelService();