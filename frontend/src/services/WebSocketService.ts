import { Client, type StompSubscription } from "@stomp/stompjs";
import type { ChatMessageRequest } from "../types/ChatMessageRequest";
import type { MessageResponse } from "../types/MessageResponse";
import type { PresenceResponse } from "../types/PresenceResponse";
import { useAuthStore } from "../stores/authStore";
import { usePresenceStore } from "../stores/presenceStore";

class WebSocketService {
  private client: Client | null = null;
  private channelSubscription: StompSubscription | null = null;
  private presenceSubscription: StompSubscription | null = null;

  connect(): Promise<void> {
    if (this.client?.connected) {
      return Promise.resolve();
    }

const token = useAuthStore.getState().token;

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
        this.subscribeToPresence();
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
    this.channelSubscription?.unsubscribe();
    this.channelSubscription = null;

    this.presenceSubscription?.unsubscribe();
    this.presenceSubscription = null;

    this.client?.deactivate();
  }

  subscribe(
  channelId: number,
  callback: (message: MessageResponse) => void
) {
  if (!this.client?.connected) {
    throw new Error("WebSocket is not connected.");
  }

  this.channelSubscription?.unsubscribe();

  this.channelSubscription = this.client.subscribe(
    `/topic/channel/${channelId}`,
    (frame) => {
        callback(JSON.parse(frame.body));
    },
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

subscribeToPresence() {
  if (!this.client?.connected) {
        throw new Error("WebSocket is not connected.");
    }

    this.presenceSubscription?.unsubscribe();

    this.presenceSubscription = this.client.subscribe(
        "/topic/presence",
        (message) => {

            const presence: PresenceResponse =
                JSON.parse(message.body);

            usePresenceStore
                .getState()
                .updatePresence(presence);

        },
    );
}}

export default new WebSocketService();