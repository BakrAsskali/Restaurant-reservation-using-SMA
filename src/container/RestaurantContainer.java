package container;

import agents.RestaurantAgent;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RestaurantContainer extends Application {

    public static List<RestaurantInfo> restaurantInfos;
    public static int numberOfRestaurants = 0;

    public static void main(String[] args) {
        launch(args);
    }

    public static AID getRestaurantAgent(int random) {
        return new AID("restaurant" + random, AID.ISLOCALNAME);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1000, 400);

        FlowPane inputPane = new FlowPane();
        inputPane.setHgap(10);
        inputPane.setVgap(10);
        inputPane.getStyleClass().add("pane");

        Label lblNumberOfRestaurants = new Label("Number of Restaurants:");
        TextField txtNumberOfRestaurants = new TextField();
        Button btnSend = new Button("Send");

        inputPane.getChildren().addAll(lblNumberOfRestaurants, txtNumberOfRestaurants, btnSend);
        root.setTop(inputPane);

        btnSend.setOnAction(event -> {
            numberOfRestaurants = Integer.parseInt(txtNumberOfRestaurants.getText());
            showRestaurantInfoInterface(numberOfRestaurants, root);
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Restaurant Interface");
        inputPane.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px; -fx-border-color: #ccc; -fx-border-width: 1px;");

        lblNumberOfRestaurants.setStyle("-fx-font-weight: bold;");
        txtNumberOfRestaurants.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-width: 1px;");
        btnSend.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        primaryStage.show();
    }

    private void showRestaurantInfoInterface(int numberOfRestaurants, BorderPane root) {
        restaurantInfos = new ArrayList<>();

        FlowPane restaurantPane = new FlowPane();
        restaurantPane.setHgap(10);
        restaurantPane.setVgap(10);
        restaurantPane.getStyleClass().add("pane");

        Random random = new Random();

        for (int i = 1; i <= numberOfRestaurants; i++) {
            int capacity = random.nextInt(10) + 1; // Generate a random capacity between 1 and 10
            String name = generateRandomName();

            Label lblName = new Label("Restaurant " + i + " Name:");
            TextField txtName = new TextField(name);
            Label lblCapacity = new Label("Capacity:");
            TextField txtCapacity = new TextField(String.valueOf(capacity));

            restaurantPane.getChildren().addAll(lblName, txtName, lblCapacity, txtCapacity);

            restaurantPane.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px; -fx-border-color: #ccc; -fx-border-width: 1px;");

            lblName.setStyle("-fx-font-weight: bold;");
            txtName.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-width: 1px;");
            lblCapacity.setStyle("-fx-font-weight: bold;");
            txtCapacity.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-width: 1px;");
            restaurantInfos.add(new RestaurantInfo(txtName, txtCapacity));
        }

        Button btnValidate = new Button("Validate");
        btnValidate.setOnAction(event -> createRestaurantAgents(root));
        restaurantPane.getChildren().add(btnValidate);

        btnValidate.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        root.setCenter(restaurantPane);
    }

    private void createRestaurantAgents(BorderPane root) {
        String containerName = "restaurant-container";
        Profile profile = new ProfileImpl(false);
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.CONTAINER_NAME, containerName);

        try {
            AgentContainer agentContainer = Runtime.instance().createAgentContainer(profile);

            List<String> restaurantNames = new ArrayList<>();
            for (RestaurantInfo restaurantInfo : restaurantInfos) {
                restaurantNames.add(restaurantInfo.getRestaurantName());
            }

            for (int i = 0; i < restaurantInfos.size(); i++) {
                RestaurantInfo restaurantInfo = restaurantInfos.get(i);
                String restaurantName = restaurantInfo.getRestaurantName();
                int restaurantCapacity = restaurantInfo.getRestaurantCapacity();

                Object[] agentArgs = new Object[]{restaurantName, restaurantCapacity};
                AgentController agentController = agentContainer.createNewAgent("restaurant" + (i + 1), RestaurantAgent.class.getName(), agentArgs);
                agentController.start();
            }

            Stage stage = (Stage) root.getScene().getWindow();

            root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            stage.close(); // Close the window after creating agents
            displayPersonneContainer();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    private void displayPersonneContainer() {
        PersonneContainer personneContainer = new PersonneContainer();
        try {
            Platform.runLater(() -> {
                try {
                    personneContainer.start(new Stage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateRandomName() {
        Random random = new Random();
        StringBuilder nameBuilder = new StringBuilder();
        for (int j = 0; j < 6; j++) {
            nameBuilder.append((char) ('A' + random.nextInt(26)));
        }
        return nameBuilder.toString();
    }

    public static class RestaurantInfo {
        public TextField nameTextField;
        private TextField capacityTextField;

        public RestaurantInfo(TextField nameTextField, TextField capacityTextField) {
            this.nameTextField = nameTextField;
            this.capacityTextField = capacityTextField;
        }

        public String getRestaurantName() {
            return nameTextField.getText();
        }

        public int getRestaurantCapacity() {
            String capacityStr = capacityTextField.getText();
            return Integer.parseInt(capacityStr);
        }
    }

}
