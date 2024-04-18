package container;

import agents.PersonneAgent;
import jade.wrapper.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public TableView<AgentController> tableView;

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

        tableView = new TableView<>();
        TableColumn<AgentController, String> idColumn = new TableColumn<>("Agent ID");
        idColumn.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getName());
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        });
        tableView.getColumns().add(idColumn);
        root.setCenter(tableView);

        btnDeployer.setOnAction(_ -> {
            String n = textFieldN.getText();
            setClientCount(Integer.parseInt(n));
            jade.core.Runtime runtime = jade.core.Runtime.instance();
            Profile profile = new ProfileImpl(false);
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.CONTAINER_NAME, "client");
            profile.setParameter("jade_core_messaging_MessageManager_poolsize", "100");
            AgentContainer agentContainer = runtime.createAgentContainer(profile);
            ObservableList<AgentController> agents = FXCollections.observableArrayList();
            for (int i = 1; i <= getClientCount(); i++) {
                try {
                    Object[] agentArgs = new Object[]{i};
                    AgentController agentController = agentContainer.createNewAgent("client" + i, PersonneAgent.class.getName(), agentArgs);
                    agentController.start();
                    agents.add(agentController);
                } catch (ControllerException e) {
                    e.printStackTrace();
                }
            }
            tableView.setItems(agents);
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
