import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import type { CreateChannelRequest } from "../../types/CreateChannelRequest";
import ChannelService from "../../services/ChannelService";
import type { ChannelType } from "../../types/ChannelType";

function CreateChannel() {

    const navigate = useNavigate();

    const { workspaceId } = useParams();

    const [form, setForm] = useState<CreateChannelRequest>({
    name: "",
    description: "",
    type: "PRIVATE",
});

    const [error, setError] = useState("");

    const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
) => {
    const { name, value } = e.target;

    setForm({
        ...form,
        [name]: name === "type"
            ? (value as ChannelType)
            : value,
    });
};

    const handleSubmit = async (
        e: React.FormEvent
    ) => {

        e.preventDefault();

        try {

            await ChannelService.createChannel(
                Number(workspaceId),
                form
            );

            navigate(`/workspaces/${workspaceId}`);

        } catch (err: any) {

            setError(
                err.response?.data?.message ??
                "Unable to create channel."
            );

        }

    };

    return (

        <div className="flex justify-center mt-10">

            <form
                onSubmit={handleSubmit}
                className="w-full max-w-lg bg-white shadow rounded p-6"
            >

                <h1 className="text-2xl font-bold mb-6">
                    Create Channel
                </h1>

                {error && (
                    <p className="text-red-500 mb-4">
                        {error}
                    </p>
                )}

                <div className="mb-4">

                    <label className="block mb-2">
                        Name
                    </label>

                    <input
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                        className="w-full border rounded p-2"
                        required
                    />

                </div>

                <div className="mb-4">

                    <label className="block mb-2">
                        Description
                    </label>

                    <textarea
                        name="description"
                        value={form.description}
                        onChange={handleChange}
                        className="w-full border rounded p-2"
                    />

                </div>

                <div className="mb-6">

                    <label className="block mb-2">
                        Type
                    </label>

                    <select
                        name="type"
                        value={form.type}
                        onChange={handleChange}
                        className="w-full border rounded p-2"
                    >

                        <option value="PUBLIC">
                            Public
                        </option>

                        <option value="PRIVATE">
                            Private
                        </option>

                    </select>

                </div>

                <button
                    type="submit"
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                >
                    Create Channel
                </button>

            </form>

        </div>

    );

}

export default CreateChannel;