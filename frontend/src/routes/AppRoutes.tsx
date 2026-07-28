import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Register from "../pages/Register/Register";
import Login from "../pages/Login/Login";
import WorkspaceDashboard from "../pages/Workspace/WorkspaceDashboard";
import CreateWorkspace from "../pages/Workspace/CreateWorkspace";
import CreateChannel from "../pages/Channel/CreateChannel";

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Redirect "/" to "/register" */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* Register Page */}
        <Route path="/register" element={<Register />} />

        {/* Login Page */}
        <Route path="/login" element={<Login />} />

        {/* Dashboard */}
        <Route path="/dashboard" element={<WorkspaceDashboard />} />

        <Route path="/workspaces/new" element={<CreateWorkspace />} />
        <Route
          path="/workspaces/:workspaceId/channels/new"
          element={<CreateChannel />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;
