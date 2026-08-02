import { Client, type StompSubscription } from "@stomp/stompjs";
import type { ChatMessageRequest } from "../types/ChatMessageRequest";
import type { MessageResponse } from "../types/MessageResponse";

class WebSocketService {
  private client: Client | null = null;
  private subscription: StompSubscription | null = null;

  connect(): Promise<void> {
    if (this.client?.connected) {
      return Promise.resolve();
    }

    const token = localStorage.getItem("token");

    if (!token) {
      return Promise.reject(new Error("JWT token not found."));
    }

    return new Promise((resolve, reject) => {
      this.client = new Client({
        brokerURL: `${import.meta.env.VITE_WS_URL}?token=${token}`,
        reconnectDelay: 5000,

        debug: (message) => {
          console.log(message);
        },
      });

      this.client.onConnect = () => {
        console.log("WebSocket connected.");
        resolve();
      };

      this.client.onStompError = (frame) => {
        console.error("Broker error:", frame.headers["message"]);
        console.error(frame.body);
      };

      this.client.onWebSocketError = (event) => {
        console.error("WebSocket error:", event);
        reject(event);
      };

      this.client.activate();
    });
  }

  disconnect() {
    this.subscription?.unsubscribe();
    this.subscription = null;

    this.client?.deactivate();
  }

  subscribe(
  channelId: number,
  callback: (message: MessageResponse) => void
) {
  if (!this.client?.connected) {
    throw new Error("WebSocket is not connected.");
  }

  this.subscription?.unsubscribe();

  this.subscription = this.client.subscribe(
    `/topic/channel/${channelId}`,
    (frame) => {
      callback(JSON.parse(frame.body));
    }
  );
}

send(request: ChatMessageRequest) {
  if (!this.client?.connected) {
    throw new Error("WebSocket is not connected.");
  }

  this.client.publish({
    destination: "/app/chat.send",
    body: JSON.stringify(request),
  });
}
}

export default new WebSocketService();