import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import AuthService from "../../services/AuthService";
import type { LoginRequest } from "../../types/LoginRequest";

function Login() {

    const navigate = useNavigate();

    const [form, setForm] = useState<LoginRequest>({
        identifier: "",
        password: ""
    });

    const [error, setError] = useState("");

    const handleChange = (
        event: React.ChangeEvent<HTMLInputElement>
    ) => {

        setForm({
            ...form,
            [event.target.name]: event.target.value
        });

    };

    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>
    ) => {

        event.preventDefault();

        setError("");

        try {

            await AuthService.login(form);

            navigate("/chat");

        } catch (err: any) {

            setError(
                err.response?.data?.message ??
                "Login failed."
            );

        }

    };

    return (

        <div className="min-h-screen flex items-center justify-center bg-gray-100">

            <form
                onSubmit={handleSubmit}
                className="bg-white p-8 rounded-xl shadow-md w-96 space-y-4"
            >

                <h1 className="text-2xl font-bold text-center">
                    Login
                </h1>

                {
                    error &&
                    <p className="text-red-500 text-center">
                        {error}
                    </p>
                }

                <input
                    type="text"
                    name="identifier"
                    placeholder="Username or Email"
                    value={form.identifier}
                    onChange={handleChange}
                    className="w-full border rounded p-2"
                />

                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={form.password}
                    onChange={handleChange}
                    className="w-full border rounded p-2"
                />

                <button
                    type="submit"
                    className="w-full bg-blue-600 text-white rounded p-2 hover:bg-blue-700"
                >
                    Login
                </button>

                <p className="text-center text-sm">
                    Don't have an account?{" "}
                    <Link
                        to="/register"
                        className="text-blue-600 hover:underline"
                    >
                        Register
                    </Link>
                </p>

            </form>

        </div>

    );

}

export default Login;