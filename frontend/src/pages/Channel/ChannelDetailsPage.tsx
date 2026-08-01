import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import client from "../../websocket/websocket";
import MessageService from "../../services/MessageService";

import type { IMessage, StompSubscription } from "@stomp/stompjs";
import type { MessageResponse } from "../../types/MessageResponse";

function ChannelDetailsPage() {
  const { channelId } = useParams();

  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [content, setContent] = useState("");

  const loadMessages = async () => {
    if (!channelId) {
      return;
    }

    try {
      const history = await MessageService.getMessages(Number(channelId));
      setMessages(history);
    } catch (error) {
      console.error(error);
    }
  };

  const handleIncomingMessage = (message: IMessage) => {
    const newMessage: MessageResponse = JSON.parse(message.body);

    setMessages((previous) => {
      if (previous.some((m) => m.id === newMessage.id)) {
        return previous;
      }

      return [...previous, newMessage];
    });
  };

  useEffect(() => {
    loadMessages();
  }, [channelId]);

  useEffect(() => {
    if (!channelId) {
      return;
    }

    let subscription: StompSubscription | undefined;

    client.onConnect = () => {
      subscription = client.subscribe(
        `/topic/channel/${channelId}`,
        handleIncomingMessage
      );
    };

    client.onStompError = (frame) => {
      console.error("STOMP Error:", frame);
    };

    client.onWebSocketError = (event) => {
      console.error("WebSocket Error:", event);
    };

    if (!client.active) {
      client.activate();
    } else if (client.connected) {
      subscription = client.subscribe(
        `/topic/channel/${channelId}`,
        handleIncomingMessage
      );
    }

    return () => {
      subscription?.unsubscribe();
    };
  }, [channelId]);

  function handleSendMessage() {
    if (!channelId || content.trim() === "") {
      return;
    }

    if (!client.connected) {
      alert("Not connected to the server.");
      return;
    }

    client.publish({
      destination: "/app/chat.send",
      body: JSON.stringify({
        channelId: Number(channelId),
        content,
        type: "TEXT",
      }),
    });

    setContent("");
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
          <p className="text-gray-500">No messages yet.</p>
        ) : (
          messages.map((message) => (
            <div
              key={message.id}
              className="border rounded-lg p-3"
            >
              <div className="font-semibold">
                {message.senderDisplayName}
              </div>

              <div>{message.content}</div>
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
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              handleSendMessage();
            }
          }}
        />

        <button
          onClick={handleSendMessage}
          disabled={!client.connected}
          className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          Send
        </button>
      </div>
    </div>
  );
}

export default ChannelDetailsPage;