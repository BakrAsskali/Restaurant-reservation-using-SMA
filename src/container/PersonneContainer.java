package container;

import agents.PersonneAgent;
import jade.wrapper.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PersonneContainer extends Application {
    private BorderPane root;
    private TextField textField;
    private int clientCount = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Container Client");

        root = new BorderPane();

        VBox topPane = new VBox(10);
        topPane.setPadding(new Insets(20));
        topPane.setStyle("-fx-background-color: #FFBB98;");

        Label labelN = new Label("Number of people (N):");
        labelN.setTextFill(Color.WHITE);
        labelN.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField textFieldN = new TextField();
        Button btnDeployer = new Button("Deploy Agents");
        btnDeployer.setStyle("-fx-background-color: #7D8E95; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: Arial;");

        topPane.getChildren().addAll(labelN, textFieldN, btnDeployer);
        root.setTop(topPane);

        btnDeployer.setOnAction(_ -> {
            String n = textFieldN.getText();
            setClientCount(Integer.parseInt(n));
            jade.core.Runtime runtime = jade.core.Runtime.instance();
            Profile profile = new ProfileImpl(false);
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.CONTAINER_NAME, "client");
            AgentContainer agentContainer = runtime.createAgentContainer(profile);
            for (int i = 1; i <= getClientCount(); i++) {
                try {
                    Object[] agentArgs = new Object[]{i};
                    AgentController agentController = agentContainer.createNewAgent("client" + i, PersonneAgent.class.getName(), agentArgs);
                    agentController.start();
                } catch (ControllerException e) {
                    e.printStackTrace();
                }
            }
        });
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public int getClientCount() {
        return clientCount;
    }

    public void setClientCount(int clientCount) {
        this.clientCount = clientCount;
    }
}
