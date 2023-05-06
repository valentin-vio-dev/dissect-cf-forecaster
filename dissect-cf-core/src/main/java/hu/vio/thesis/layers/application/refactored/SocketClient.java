package hu.vio.thesis.layers.application.refactored;

import hu.vio.thesis.layers.application.Logger;
import hu.vio.thesis.layers.application.Utils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;
    private String host;
    private int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SocketClient startConnection() throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        connected = true;
        return this;
    }

    private JSONObject sendMessage(MessageProtocol messageProtocol) throws IOException {
        out.println(messageProtocol.toString());
        return new JSONObject(in.readLine());
    }

    // https://stackoverflow.com/questions/17667903/python-socket-receive-large-amount-of-data
    private JSONObject sendMessageSize(MessageProtocol messageProtocol) throws IOException {
        out.println(
                new MessageProtocol(
                    MessageProtocol.Command.OTHER,
                "socket-message-size",
                    messageProtocol.toString().getBytes().length
                )
        );
        return new JSONObject(in.readLine());
    }
    public JSONObject send(MessageProtocol messageProtocol) throws IOException {
        if (!connected) {
            throw new Error("Socket is not running!");
        }

        JSONObject messageSizeResult = sendMessageSize(messageProtocol);

        if (!(messageSizeResult.get("event").equals("socket-message-size") && messageSizeResult.get("message").equals("OK"))) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        Logger.log("[SOCKET][OUT]", Utils.strToStrSpace(messageProtocol.toString(), 75));

        JSONObject response = sendMessage(messageProtocol);

        long elapsedTime = System.currentTimeMillis() - startTime;
        Logger.log(String.format("[SOCKET][IN][TIME:%s]", elapsedTime), Utils.strToStrSpace(response.toString(), 75));

        return response;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
