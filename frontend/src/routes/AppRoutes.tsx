import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Register from "../pages/Register/Register";
import Login from "../pages/Login/Login";
import Dashboard from "../pages/Dashboard/Dashboard";

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>

                {/* Redirect "/" to "/register" */}
                <Route
                    path="/"
                    element={<Navigate to="/register" replace />}
                />

                {/* Register Page */}
                <Route
                    path="/register"
                    element={<Register />}
                />

                {/* Login Page */}
                <Route
                    path="/login"
                    element={<Login />}
                />

                {/* Dashboard */}
                <Route
                    path="/dashboard"
                    element={<Dashboard />}
                />

            </Routes>
        </BrowserRouter>
    );
}

export default AppRoutes;