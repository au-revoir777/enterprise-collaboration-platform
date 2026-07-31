import type { MessageType } from "./MessageType";

export interface CreateMessageRequest {
    content: string;
    type: MessageType;
}