import type { MessageType } from "./MessageType";

export interface ChatMessageRequest {
    channelId: number;
    content: string;
    type: MessageType;
}