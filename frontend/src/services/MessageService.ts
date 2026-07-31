import api from "../api/axios"

import type { CreateMessageRequest } from "../types/CreateMessageRequest";
import type { MessageResponse } from "../types/MessageResponse";

class MessageService{
    async sendMessage(
        channelId: number,
        request: CreateMessageRequest
    ): Promise<MessageResponse> {

        const response = await api.post<MessageResponse>(
            `/channels/${channelId}/messages`,
            request
        );

        return response.data;
    }

    async getMessages(
        channelId: number
    ): Promise<MessageResponse[]> {

        const response = await api.get<MessageResponse[]>(
            `/channels/${channelId}/messages`
        );

        return response.data;
    }
}

export default new MessageService;