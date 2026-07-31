import type { MessageType } from "./MessageType";

export interface MessageResponse {
    id: number;
    content: string;
    type: MessageType;
    senderId: number;
    senderUsername: string;
    senderDisplayName: string;
    createdAt: string;
    edited: boolean;
}