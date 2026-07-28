import type { ChannelType } from "./ChannelType";

export interface CreateChannelRequest {
    name: string;
    description: string;
    type: ChannelType;
}