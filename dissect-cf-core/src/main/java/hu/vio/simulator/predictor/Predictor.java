package hu.vio.simulator.predictor;

import hu.vio.simulator.Logger;
import hu.vio.simulator.SocketClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class Predictor {
    private final AbstractPredictor abstractPredictor;
    private SocketClient client;
    private final int chunkSize;
    private final int smooth;

    public Predictor(AbstractPredictor abstractPredictor, int chunkSize, int smooth) {
        this.abstractPredictor = abstractPredictor;
        this.chunkSize = chunkSize;
        this.smooth = smooth;
        this.connect();
    }

    private void connect() {
        client = new SocketClient();
        try {
            client.startConnection("127.0.0.1", 65432);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public List<Double> predict() {
        Logger.log("Collects", String.valueOf(abstractPredictor.getHistory().size()), "(" + chunkSize + ")");
        if (abstractPredictor.getHistory().size() >= chunkSize) {
            Logger.log("Data sent!");
            List<Double> data = abstractPredictor.predict(this);
            abstractPredictor.clearHistory();
        }

        return null;
    }

    public String getName() {
        return abstractPredictor.getName();
    }

    public void send() {
        double[] data = new double[abstractPredictor.getHistory().size()];
        for (int i = 0; i < abstractPredictor.getHistory().size(); i++) {
            double d = (Double) abstractPredictor.getHistory().get(i).getFeatureList().get(4).data;
            data[i] = d;
        }

        JSONObject json = new JSONObject();
        json.put("predictor", abstractPredictor.getName());
        json.put("train_size", 0.75);
        json.put("smooth", smooth);
        json.put("chunk_size", chunkSize);
        json.put("data", data);
        json.put("data_size", data.length);

        try {
            Logger.log("Data:", json.toString());
            long startTime = System.currentTimeMillis();
            String responseString = client.sendMessage(json.toString());
            JSONObject responseJSON = new JSONObject(responseString);
            long endTime = System.currentTimeMillis();
            Logger.log("Result:", responseJSON.toString());
            Logger.log("Exc. time:", (endTime - startTime) + " millis");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getSmooth() {
        return smooth;
    }

    public AbstractPredictor getAbstractPredictor() {
        return abstractPredictor;
    }
}
