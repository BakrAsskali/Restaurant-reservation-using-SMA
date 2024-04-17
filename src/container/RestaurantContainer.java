package container;

import agents.PersonneAgent;
import agents.RestaurantAgent;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RestaurantContainer extends Application {

    private List<RestaurantInfo> restaurantInfos;

    public static void main(String[] args) {
        launch(args);
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
            int numberOfRestaurants = Integer.parseInt(txtNumberOfRestaurants.getText());
            showRestaurantInfoInterface(numberOfRestaurants, root);
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Restaurant Interface");
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

            restaurantInfos.add(new RestaurantInfo(txtName, txtCapacity));
        }

        Button btnValidate = new Button("Validate");
        btnValidate.setOnAction(event -> createRestaurantAgents(root));
        restaurantPane.getChildren().add(btnValidate);

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

    private static class RestaurantInfo {
        private TextField nameTextField;
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

    public List<String> getRestaurantNames() {
        List<String> restaurantNames = new ArrayList<>();
        for (RestaurantInfo restaurantInfo : restaurantInfos) {
            restaurantNames.add(restaurantInfo.getRestaurantName());
        }
        return restaurantNames;
    }

    public List<Integer> getCapacities() {
        List<Integer> capacities = new ArrayList<>();
        for (RestaurantInfo restaurantInfo : restaurantInfos) {
            capacities.add(restaurantInfo.getRestaurantCapacity());
        }
        return capacities;
    }
}
