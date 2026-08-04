import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Register from "../pages/Register/Register";
import Login from "../pages/Login/Login";
import WorkspaceDashboard from "../pages/Workspace/WorkspaceDashboard";
import CreateWorkspace from "../pages/Workspace/CreateWorkspace";
import CreateChannel from "../pages/Channel/CreateChannel";
import WorkspaceDetails from "../pages/Workspace/WorkspaceDetails";
import ChannelDetailsPage from "../pages/Channel/ChannelDetailsPage";
import ProtectedLayout from "../layouts/ProtectedLayout";

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

        <Route element={<ProtectedLayout />}>

    <Route
        path="/dashboard"
        element={<WorkspaceDashboard />}
    />

    <Route
        path="/workspaces/new"
        element={<CreateWorkspace />}
    />

    <Route
        path="/workspaces/:workspaceId"
        element={<WorkspaceDetails />}
    />

    <Route
        path="/workspaces/:workspaceId/channels/new"
        element={<CreateChannel />}
    />

    <Route
        path="/channels/:channelId/messages"
        element={<ChannelDetailsPage />}
    />

</Route>
      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;
