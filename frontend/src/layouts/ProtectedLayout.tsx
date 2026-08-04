import { Navigate, Outlet } from "react-router-dom";

import { useAuthStore } from "../stores/authStore";

import { useEffect } from "react";

import WebSocketService from "../services/WebSocketService";
import PresenceService from "../services/PresenceService";

import { usePresenceStore } from "../stores/presenceStore";



function ProtectedLayout() {
  const token = useAuthStore((state) => state.token);

  useEffect(() => {

     if (!token) {
        return;
    }
    const initialize = async () => {

        try {

            await WebSocketService.connect();

            const presences =
                await PresenceService.getPresences();

            usePresenceStore
                .getState()
                .initialize(presences);

        } catch (error) {

            console.error("Application initialization failed:", error);
            
        }

    };

    initialize();

    return () => {

        WebSocketService.disconnect();

        usePresenceStore
            .getState()
            .clear();

    };

}, [token]);

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

export default ProtectedLayout;
