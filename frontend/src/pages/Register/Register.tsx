import { useState } from "react";
import { useNavigate } from "react-router-dom";

import AuthService from "../../services/AuthService";
import type { RegisterRequest } from "../../types/RegisterRequest";

function Register() {

    const navigate = useNavigate();

    const [form, setForm] = useState<RegisterRequest>({
        username: "",
        displayName: "",
        email: "",
        password: "",
        confirmPassword: "",
        profilePictureUrl: ""
    });

    const [error, setError] = useState("");

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
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

            await AuthService.register(form);

            navigate("/login");

        } catch (err: any) {

            setError(
                err.response?.data?.message ??
                "Registration failed."
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
                    Register
                </h1>

                {
                    error &&
                    <p className="text-red-500 text-center">
                        {error}
                    </p>
                }

                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={form.username}
                    onChange={handleChange}
                    className="w-full border rounded p-2"
                />

                <input
                    type="text"
                    name="displayName"
                    placeholder="Display Name"
                    value={form.displayName}
                    onChange={handleChange}
                    className="w-full border rounded p-2"
                />

                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={form.email}
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

                <input
                    type="password"
                    name="confirmPassword"
                    placeholder="Confirm Password"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    className="w-full border rounded p-2"
                />

                <input
                    type="text"
                    name="profilePictureUrl"
                    placeholder="Profile Picture URL (Optional)"
                    value={form.profilePictureUrl}
                    onChange={handleChange}
                    className="w-full border rounded p-2"
                />

                <button
                    type="submit"
                    className="w-full bg-blue-600 text-white rounded p-2 hover:bg-blue-700"
                >
                    Register
                </button>

            </form>

        </div>

    );

}

export default Register;