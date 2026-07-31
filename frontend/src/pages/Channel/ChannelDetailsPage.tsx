import { useState } from "react";
import { useParams } from "react-router-dom";

import MessageService from "../../services/MessageService";

import type { CreateMessageRequest } from "../../types/CreateMessageRequest";
import type { MessageResponse } from "../../types/MessageResponse";

function ChannelDetailsPage() {
    const { channelId } = useParams();

    const [messages, setMessages] = useState<MessageResponse[]>([]);
    const [content, setContent] = useState("");

    async function handleSendMessage() {

        if (!channelId || content.trim() === "") {
            return;
        }

        const request: CreateMessageRequest = {
            content,
            type: "TEXT"
        };

        try {

            const message = await MessageService.sendMessage(
                Number(channelId),
                request
            );

            setMessages(previous => [...previous, message]);

            setContent("");

        } catch (error) {
            console.error(error);
            alert("Failed to send message.");
        }
    }

    return (
        <div className="flex flex-col h-screen">

            <div className="border-b p-4">
                <h1 className="text-2xl font-bold">
                    Channel #{channelId}
                </h1>
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-4">

                {messages.length === 0 ? (
                    <p className="text-gray-500">
                        No messages yet.
                    </p>
                ) : (
                    messages.map(message => (
                        <div
                            key={message.id}
                            className="border rounded-lg p-3"
                        >
                            <div className="font-semibold">
                                {message.senderDisplayName}
                            </div>

                            <div>
                                {message.content}
                            </div>
                        </div>
                    ))
                )}

            </div>

            <div className="border-t p-4 flex gap-3">

                <input
                    type="text"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    placeholder="Type a message..."
                    className="flex-1 border rounded-lg px-3 py-2"
                />

                <button
                    onClick={handleSendMessage}
                    className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700"
                >
                    Send
                </button>

            </div>

        </div>
    );
}

export default ChannelDetailsPage;