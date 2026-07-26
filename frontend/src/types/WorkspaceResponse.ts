import type { Type } from "./WorkspaceType";

export interface WorkspaceResponse {
    id: number;
    name: string;
    description: string;
    logoUrl: string;
    type: Type;
    ownerUsername: string;
    createdAt: string;
}