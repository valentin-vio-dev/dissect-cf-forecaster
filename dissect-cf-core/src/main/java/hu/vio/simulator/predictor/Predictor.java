package hu.vio.simulator.predictor;

import hu.vio.simulator.ComputeNodeData;
import hu.vio.simulator.Feature;
import hu.vio.simulator.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;

public class Predictor {
    private final AbstractPredictor abstractPredictor;
    private final String SCRIPT_PATH = Utils.getRoot() + "/dissect-cf-core/src/main/java/hu/vio/simulator/predictor/python/main.py";

    public Predictor(AbstractPredictor abstractPredictor) {
        this.abstractPredictor = abstractPredictor;
        this.createDatabaseTablesIfNeeded();
    }

    private void clearDatabaseTables() {
        Connection connection = null;
        try {
            connection = connect();

            Statement stmt_clear = connection.createStatement();
            stmt_clear.execute("DELETE FROM Data");

            Statement stmt_clear2 = connection.createStatement();
            stmt_clear2.execute("DELETE FROM Pred");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Double> predict() {
        System.out.println("Called Predictor... (" + abstractPredictor.getHistory().size() + ")");
        if (abstractPredictor.getHistory().size() >= 256) {
            this.exportHistory();
            List<Double> data = abstractPredictor.predict(this);
            clearDatabaseTables();
            abstractPredictor.clearHistory();
            System.out.println("Exported data!");
        }

        return null;
    }

    public String getName() {
        return abstractPredictor.getName();
    }

    public void executeScript() {
        String fetching = "python -W ignore " + SCRIPT_PATH + " --predictor=" + abstractPredictor.getName() + " --train_size=0.75 --smooth=20";
        String[] commandToExecute = new String[]{ "cmd.exe", "/c", fetching };
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(commandToExecute);
            process.waitFor();

            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;

            while ((line = inputStream.readLine()) != null) {
                System.err.println(line);
            }

            inputStream.close();

            while ((line = errorStream.readLine()) != null) {
                System.err.println(line);
            }

            errorStream.close();

            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private Connection connect() throws SQLException {
        String url = "jdbc:sqlite:" + Utils.getRoot() + "/dissect-cf-core/src/main/java/hu/vio/simulator/predictor/python/pred_database.db";
        return DriverManager.getConnection(url);
    }

    private void createDatabaseTablesIfNeeded() {
        Connection connection = null;
        try {
            connection = connect();
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Data(id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT);");

            Statement stmt2 = connection.createStatement();
            stmt2.execute("CREATE TABLE IF NOT EXISTS Pred(id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT);");

            clearDatabaseTables();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void exportHistory() {
        Connection connection = null;

        try{
            connection = connect();
            for (ComputeNodeData data: abstractPredictor.getHistory()) {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Data(data) VALUES(?);");
                pstmt.setString(1, ComputeNodeData.getColumns() + "=" + data.toString());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public AbstractPredictor getAbstractPredictor() {
        return abstractPredictor;
    }
}
