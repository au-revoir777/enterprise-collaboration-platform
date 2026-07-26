import { useState } from "react";
import { useNavigate } from "react-router-dom";

import WorkspaceService from "../../services/WorkspaceService";
import type { CreateWorkspaceRequest } from "../../types/CreateWorkspaceRequest";

function CreateWorkspace() {
    const navigate = useNavigate();

    const [form, setForm] = useState<CreateWorkspaceRequest>({
        name: "",
        description: "",
        type: "PRIVATE",
        logoUrl: "",
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) => {
        const { name, value } = e.target;

        setForm((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            setLoading(true);
            setError("");

            await WorkspaceService.createWorkspace(form);

            navigate("/dashboard");
        } catch (err: any) {
            setError(
                err?.response?.data?.message ??
                "Unable to create workspace."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-100">
            <div className="w-full max-w-lg rounded-lg bg-white p-8 shadow-lg">

                <h1 className="mb-6 text-center text-3xl font-bold">
                    Create Workspace
                </h1>

                {error && (
                    <div className="mb-4 rounded bg-red-100 p-3 text-red-700">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-5">

                    <div>
                        <label className="mb-2 block font-medium">
                            Workspace Name
                        </label>

                        <input
                            type="text"
                            name="name"
                            value={form.name}
                            onChange={handleChange}
                            className="w-full rounded border p-3"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-2 block font-medium">
                            Description
                        </label>

                        <textarea
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            rows={3}
                            className="w-full rounded border p-3"
                        />
                    </div>

                    <div>
                        <label className="mb-2 block font-medium">
                            Workspace Type
                        </label>

                        <select
                            name="type"
                            value={form.type}
                            onChange={handleChange}
                            className="w-full rounded border p-3"
                        >
                            <option value="PRIVATE">
                                Private
                            </option>

                            <option value="PUBLIC">
                                Public
                            </option>
                        </select>
                    </div>

                    <div>
                        <label className="mb-2 block font-medium">
                            Logo URL
                        </label>

                        <input
                            type="text"
                            name="logoUrl"
                            value={form.logoUrl}
                            onChange={handleChange}
                            className="w-full rounded border p-3"
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full rounded bg-blue-600 py-3 text-white hover:bg-blue-700 disabled:opacity-50"
                    >
                        {loading ? "Creating..." : "Create Workspace"}
                    </button>

                </form>

            </div>
        </div>
    );
}

export default CreateWorkspace;