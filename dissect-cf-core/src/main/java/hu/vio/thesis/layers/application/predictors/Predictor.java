package hu.vio.thesis.predictors;

import hu.vio.thesis.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class Predictor {
    private final String name;
    private final int chunkSize;
    private final int smooth;
    private final double trainSize;

    private SocketClient client;

    private ComputingNodesHandler computingNodesHandler;

    public Predictor(String name, int chunkSize, int smooth, double trainSize) {
        this.name = name;
        this.chunkSize = chunkSize;
        this.smooth = smooth;
        this.trainSize = trainSize;
    }

    public Object predict() {
        Logger.log("Predict is triggered...");
        if (client == null || !client.isConnected()) {
            return null;
        }

        try {
            // Todo
            client.sendMessage(createSocketMessage().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public JSONObject createSocketMessage() {
        JSONObject body = new JSONObject();
        body.put("predictor", name);
        body.put("train_size", trainSize);
        body.put("smooth", smooth);
        body.put("chunk_size", chunkSize);
        body.put("computing_nodes_size", computingNodesHandler.getComputingNodes().size());

        List<JSONObject> data = new ArrayList<>();
        for (ComputingNode computingNode: computingNodesHandler.getComputingNodes()) {
            JSONObject cn = new JSONObject();
            cn.put("name", computingNode.getFormatedName());

            List<JSONObject> cnData = new ArrayList<>();
            for (Map.Entry<String, List<Object>> entry: computingNode.getData().entrySet()) { // Todo last ~250 data to send
                JSONObject fn = new JSONObject();
                fn.put(entry.getKey(), entry.getValue());
                cnData.add(fn);
            }
            cn.put("values", cnData);
            data.add(cn);
        }
        body.put("data", data);

        return body;
    }

    public void startConnection(SocketClient client) {
        this.client = client;
        try {
            this.client.startConnection("127.0.0.1", 65432);
        } catch (IOException e) {
            Logger.log("No connection from predictor layer!");
        }
    }

    public String getName() {
        return name;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getSmooth() {
        return smooth;
    }

    public double getTrainSize() {
        return trainSize;
    }

    public void setComputingNodesHandler(ComputingNodesHandler computingNodesHandler) {
        this.computingNodesHandler = computingNodesHandler;
    }
}
