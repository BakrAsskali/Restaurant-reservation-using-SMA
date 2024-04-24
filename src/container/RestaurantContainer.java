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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class  RestaurantContainer extends Application {

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

        FlowPane inputPane = new FlowPane();
        inputPane.setPadding(new Insets(20));
        inputPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        inputPane.setHgap(20);
        inputPane.setVgap(100); // This gap is very large, reducing it to make space for more content
        inputPane.getStyleClass().add("pane");

        Label lblNumberOfRestaurants = new Label("Number of Restaurants:");
        TextField txtNumberOfRestaurants = new TextField();
        Button btnSend = new Button("Send");

        // Styling
        lblNumberOfRestaurants.setStyle("-fx-font-size: 14px;");
        txtNumberOfRestaurants.setStyle("-fx-font-size: 14px;");
        btnSend.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        inputPane.getChildren().addAll(lblNumberOfRestaurants, txtNumberOfRestaurants, btnSend);
        root.setTop(inputPane);

        btnSend.setOnAction(event -> {
            int numberOfRestaurants = Integer.parseInt(txtNumberOfRestaurants.getText());
            showRestaurantInfoInterface(numberOfRestaurants, root);
        });

        // Adding an image to the right side
        FlowPane imagePane = new FlowPane();
        Image image = new Image("file:///C:/Users/BIG CHOIX/Desktop/JAVA/myfolderprjt/Restaurant-reservation-using-SMA/src/Rest.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // Set width of the image
        imageView.setPreserveRatio(true); // Preserve aspect ratio
        imagePane.getChildren().add(imageView);
        root.setRight(imagePane);

        Scene scene = new Scene(root, 800, 600); // Increased width and height of the scene
        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Restaurant App");
        primaryStage.show();
    }

    private void showRestaurantInfoInterface(int numberOfRestaurants, BorderPane root) {
        List<RestaurantInfo> restaurantInfos = new ArrayList<>();

        GridPane restaurantPane = createRestaurantPane();
        populateRestaurantPane(numberOfRestaurants, restaurantInfos, restaurantPane);

        Button btnValidate = createValidateButton(restaurantInfos, root);
        restaurantPane.add(btnValidate, 0, numberOfRestaurants + 1);

        root.setCenter(restaurantPane);
    }

    private GridPane createRestaurantPane() {
        GridPane restaurantPane = new GridPane();
        restaurantPane.setHgap(10);
        restaurantPane.setVgap(10);
        restaurantPane.getStyleClass().add("pane");
        restaurantPane.setPadding(new Insets(20));
        return restaurantPane;
    }

    private void populateRestaurantPane(int numberOfRestaurants, List<RestaurantInfo> restaurantInfos, GridPane restaurantPane) {
        Random random = new Random();

        for (int i = 0; i < numberOfRestaurants; i++) {
            String name = generateRandomName();
            int capacity = random.nextInt(10) + 1;

            Label lblName = new Label("Restaurant " + (i + 1));
            TextField txtName = new TextField(name);
            Label lblCapacity = new Label("Capacity:");
            TextField txtCapacity = new TextField(String.valueOf(capacity));

            restaurantPane.addRow(i, lblName, txtName, lblCapacity, txtCapacity);
            restaurantInfos.add(new RestaurantInfo(txtName, txtCapacity));
        }
    }

    private Button createValidateButton(List<RestaurantInfo> restaurantInfos, BorderPane root) {
        Button btnValidate = new Button("Validate");
        btnValidate.setOnAction(event -> createRestaurantAgents(restaurantInfos, root));
        return btnValidate;
    }

    private void createRestaurantAgents(List<RestaurantInfo> restaurantInfos, BorderPane root) {
        String containerName = "restaurant-container";
        Profile profile = new ProfileImpl(false);
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.CONTAINER_NAME, containerName);

        try {
            AgentContainer agentContainer = Runtime.instance().createAgentContainer(profile);

            List<String> restaurantNames = new ArrayList<>();
            for (RestaurantInfo restaurantInfo : RestaurantContainer.restaurantInfos) {
                restaurantNames.add(restaurantInfo.getRestaurantName());
            }

            for (int i = 0; i < RestaurantContainer.restaurantInfos.size(); i++) {
                RestaurantInfo restaurantInfo = RestaurantContainer.restaurantInfos.get(i);
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
