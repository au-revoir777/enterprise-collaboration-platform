import api from "../api/axios";

import type { CreateWorkspaceRequest } from "../types/CreateWorkspaceRequest";
import type { WorkspaceResponse } from "../types/WorkspaceResponse";

class WorkspaceService {

    async createWorkspace(
        request: CreateWorkspaceRequest
    ): Promise<WorkspaceResponse> {

        const response = await api.post<WorkspaceResponse>(
            "/workspaces",
            request
        );

        return response.data;
    }

    async getMyWorkspaces(): Promise<WorkspaceResponse[]> {

        const response = await api.get<WorkspaceResponse[]>(
            "/workspaces"
        );

        return response.data;
    }
}

export default new WorkspaceService();