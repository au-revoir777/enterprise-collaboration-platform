import { Client } from "@stomp/stompjs";

const apiUrl = import.meta.env.VITE_API_BASE_URL;

const serverUrl = apiUrl.replace(/\/api$/, "");

const wsUrl = serverUrl.replace(/^http/, "ws");
console.log(wsUrl);

const token = localStorage.getItem("token");

const client = new Client({
    brokerURL: `${wsUrl}/ws?token=${token}`,

    reconnectDelay: 5000,

    debug: console.log,
});

export default client;