import type { ChannelType } from "./ChannelType";

export interface ChannelResponse {
    id: number;
    name: string;
    description: string;
    type: ChannelType;
    workspaceId: number;
    createdAt: string;
}