import type { Type } from "./WorkspaceType";

export interface CreateWorkspaceRequest {
    name: string;
    description: string;
    type: Type;
    logoUrl: string;
}