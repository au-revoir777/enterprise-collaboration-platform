import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import ChannelService from "../../services/ChannelService";

import type { ChannelResponse } from "../../types/ChannelResponse";

function WorkspaceDetails() {

    const { workspaceId } = useParams();

    const navigate = useNavigate();

    const [channels, setChannels] = useState<ChannelResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        const fetchChannels = async () => {

            try {

                const data =
                    await ChannelService.getWorkspaceChannels(
                        Number(workspaceId)
                    );

                setChannels(data);

            } catch (err: any) {

                setError(
                    err.response?.data?.message ??
                    "Unable to load channels."
                );

            } finally {

                setLoading(false);

            }

        };

        fetchChannels();

    }, [workspaceId]);

    if (loading) {
        return <p className="p-8">Loading...</p>;
    }

    if (error) {
        return <p className="p-8 text-red-500">{error}</p>;
    }

    return (

        <div className="max-w-4xl mx-auto mt-10">

            <div className="flex justify-between items-center mb-6">

                <h1 className="text-3xl font-bold">
                    Channels
                </h1>

                <button
                    onClick={() =>
                        navigate(
                            `/workspaces/${workspaceId}/channels/new`
                        )
                    }
                    className="bg-blue-600 text-white px-4 py-2 rounded"
                >
                    Create Channel
                </button>

            </div>

            {channels.length === 0 ? (

                <div className="text-gray-500">

                    No channels found.

                </div>

            ) : (

                <div className="space-y-4">

                    {channels.map(channel => (

                        <div
                            key={channel.id}
                            className="border rounded-lg p-4 shadow"
                        >

                            <h2 className="font-semibold text-lg">
                                {channel.name}
                            </h2>

                            <p className="text-gray-600">
                                {channel.description}
                            </p>

                            <span className="text-sm text-blue-600">
                                {channel.type}
                            </span>

                        </div>

                    ))}

                </div>

            )}

        </div>

    );

}

export default WorkspaceDetails;