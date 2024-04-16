package container;

import agents.PersonneAgent;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonneContainer extends Application {
    private BorderPane root;
    private TextField textField;
    private VBox personVBox;
    protected RestaurantContainer restaurantContainer = new RestaurantContainer();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Person Interface");
        root = new BorderPane();

        FlowPane topPane = new FlowPane();
        topPane.setPadding(new Insets(10, 10, 10, 10));
        topPane.setHgap(10);

        Label label = new Label("Number of people:");
        textField = new TextField();
        Button button = new Button("Send");

        button.setOnAction(e -> {
            int numberOfPeople = Integer.parseInt(textField.getText());
            List<String> restaurantNames = restaurantContainer.getRestaurantNames();
            List<Integer> restaurantCapacity = restaurantContainer.getCapacities();
            createPersonInterfaces(numberOfPeople, restaurantNames, restaurantCapacity);
        });

        topPane.getChildren().addAll(label, textField, button);
        root.setTop(topPane);

        personVBox = new VBox(10);
        personVBox.setPadding(new Insets(10, 10, 10, 10));

        root.setCenter(personVBox);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createPersonInterfaces(int numberOfPeople, List<String> restaurantNames, List<Integer> restaurantCapacity) {
        personVBox.getChildren().clear();
        Map<String, Integer> reservations = new HashMap<>();

        for (int i = 0; i < numberOfPeople; i++) {
            VBox personBox = new VBox(5);
            personBox.setPadding(new Insets(10, 10, 10, 10));

            Label personLabel = new Label("Person " + (i + 1));
            personBox.getChildren().add(personLabel);

            ToggleGroup toggleGroup = new ToggleGroup();
            for (String restaurant : restaurantNames) {
                RadioButton radioButton = new RadioButton(restaurant);
                radioButton.setToggleGroup(toggleGroup);
                personBox.getChildren().add(radioButton);
            }

            Button reserveButton = new Button("Reserve");
            personBox.getChildren().add(reserveButton);

            int finalI = i;
            reserveButton.setOnAction(e -> {
                RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
                if (selectedRadioButton != null) {
                    String selectedRestaurant = selectedRadioButton.getText();
                    int reservationCount = reservations.getOrDefault(selectedRestaurant, 0);

                    if (reservationCount < restaurantCapacity.get(restaurantNames.indexOf(selectedRestaurant))) {
                        reservations.put(selectedRestaurant, reservationCount + 1);
                        String message = "Reservation successful for Person " + (finalI + 1) +
                                " at restaurant " + selectedRestaurant;
                        showAlert(Alert.AlertType.INFORMATION, "Reservation", message);
                    } else {
                        String message = "Sorry, the capacity of restaurant " + selectedRestaurant +
                                " is reached. Please choose another restaurant.";
                        showAlert(Alert.AlertType.WARNING, "Reservation", message);
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Reservation", "Please select a restaurant.");
                }
            });

            personVBox.getChildren().add(personBox);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
