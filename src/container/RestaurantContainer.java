package container;

import agents.PersonneAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import javafx.application.Application;

import agents.RestaurantAgent;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RestaurantContainer extends Application{
    private Frame frame;
    private TextField textField;
    private static List<RestaurantInfo> restaurantInfos;

    // Define a set of characters from which random names will be generated
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        frame = new Frame("Restaurant Interface");
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        Panel panel = new Panel();
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: #7D8E95;");
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(10);

        Label label = new Label("Number of restaurants:");
        textField = new TextField(10); // Set preferred width
        Button button = new Button("Send");

        // Adjusting font and alignment for label
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setAlignment(Label.CENTER);

        // Adding components to panel
        panel.add(label);
        panel.add(textField);
        panel.add(button);

        // Adding panel to frame
        frame.add(panel, BorderLayout.CENTER);

        // Adding ActionListener to button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numberOfRestaurantsStr = textField.getText();
                int numberOfRestaurants = Integer.parseInt(numberOfRestaurantsStr);
                showRestaurantInfoInterface(numberOfRestaurants);
            }
        });

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);
        // Make the frame visible
        frame.setVisible(true);
    }

    private void showRestaurantInfoInterface(int numberOfRestaurants) {
        restaurantInfos = new ArrayList<>();

        Panel panel = new Panel();
        panel.setLayout(new GridLayout(numberOfRestaurants, 2, 10, 10)); // Grid layout with gaps

        Random random = new Random();

        for (int i = 1; i <= numberOfRestaurants; i++) {
            int capacity = random.nextInt(10) + 1; // Generate a random capacity between 1 and 10

            // Generate a random name
            StringBuilder nameBuilder = new StringBuilder();
            for (int j = 0; j < 6; j++) {
                nameBuilder.append((char) ('A' + random.nextInt(26)));
            }
            String name = nameBuilder.toString();

            Label nameLabel = new Label("Restaurant " + i + " Name: ");
            TextField nameTextField = new TextField(name);

            Label capacityLabel = new Label("Restaurant " + i + " Capacity: ");
            TextField capacityTextField = new TextField(Integer.toString(capacity));

            panel.add(nameLabel);
            panel.add(nameTextField);
            panel.add(capacityLabel);
            panel.add(capacityTextField);

            restaurantInfos.add(new RestaurantInfo(nameTextField, capacityTextField));
        }

        Button validateButton = new Button("Validate");
        validateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRestaurantAgents();
            }
        });

        frame.remove(frame.getComponent(0)); // Remove the previous panel
        frame.add(panel, BorderLayout.CENTER);
        frame.add(validateButton, BorderLayout.SOUTH);
        frame.pack(); // Adjust frame size
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true); // Make the frame visible
    }



    private void createRestaurantAgents() {
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

            Object[] personneArgs = new Object[]{restaurantNames};
            AgentController personneAgentController = agentContainer.createNewAgent("personne-agent", PersonneAgent.class.getName(), personneArgs);
            personneAgentController.start();

            // Create RestaurantAgents
            for (int i = 0; i < restaurantInfos.size(); i++) {
                RestaurantInfo restaurantInfo = restaurantInfos.get(i);
                String restaurantName = restaurantInfo.getRestaurantName();
                int restaurantCapacity = restaurantInfo.getRestaurantCapacity();

                Object[] agentArgs = new Object[]{restaurantName, restaurantCapacity};
                AgentController agentController = agentContainer.createNewAgent("restaurant" + (i + 1), RestaurantAgent.class.getName(), agentArgs);
                System.out.println("The agent " + restaurantName + " belongs to the container: " + agentContainer.getContainerName());
                agentController.start();
            }
            frame.dispose(); // Close the previous interface
            // Display PersonneContainer after creating agents
            displayPersonneContainer();
        } catch (ControllerException e) {
            e.printStackTrace();
        }

        frame.dispose(); // Close the previous interface
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
