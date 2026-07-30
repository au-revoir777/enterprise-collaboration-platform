import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import WorkspaceService from "../../services/WorkspaceService";
import type { WorkspaceResponse } from "../../types/WorkspaceResponse";
import { useNavigate } from "react-router-dom";

function WorkspaceDashboard() {
  const [workspaces, setWorkspaces] = useState<WorkspaceResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const loadWorkspaces = async () => {
      try {
        const response = await WorkspaceService.getMyWorkspaces();

        setWorkspaces(response);
      } catch (err: any) {
        setError(err?.response?.data?.message ?? "Unable to load workspaces.");
      } finally {
        setLoading(false);
      }
    };

    loadWorkspaces();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        Loading workspaces...
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-screen text-red-600">
        {error}
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">My Workspaces</h1>

        <Link
          to="/workspaces/new"
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Create Workspace
        </Link>
      </div>

      {workspaces.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-8 text-center">
          <h2 className="text-xl font-semibold mb-2">No workspaces yet</h2>

          <p className="text-gray-500">
            Create your first workspace to get started.
          </p>
        </div>
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {workspaces.map((workspace) => (
            <div key={workspace.id} className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-semibold mb-2">{workspace.name}</h2>

              <p className="text-gray-600 mb-4">{workspace.description}</p>

              <div className="text-sm text-gray-500">
                <p>Owner: {workspace.ownerUsername}</p>

                <p>Type: {workspace.type}</p>

                <p>
                  Created: {new Date(workspace.createdAt).toLocaleDateString()}
                </p>
              </div>
              <button
                onClick={() =>
                  navigate(`/workspaces/${workspace.id}/channels/new`)
                }
                className="mt-4 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
              >
                Create Channel
              </button>
              <button
                onClick={() => navigate(`/workspaces/${workspace.id}`)}
                className="mt-3 bg-indigo-600 text-white px-4 py-2 rounded"
              >
                Open Workspace
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default WorkspaceDashboard;
