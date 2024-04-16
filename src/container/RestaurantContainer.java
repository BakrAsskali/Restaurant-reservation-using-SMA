package container;

import agents.PersonneAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RestaurantContainer {
    private JFrame frame;
    private List<RestaurantInfo> restaurantInfos;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RestaurantContainer container = new RestaurantContainer();
            container.init();
        });
    }

    private void init() {
        frame = new JFrame("Restaurant Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Number of restaurants:");
        JTextField textField = new JTextField(10);
        JButton button = new JButton("Send");

        panel.add(label);
        panel.add(textField);
        panel.add(button);

        frame.add(panel, BorderLayout.NORTH);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String numberOfRestaurantsStr = textField.getText();
                int numberOfRestaurants = Integer.parseInt(numberOfRestaurantsStr);
                showRestaurantInfoInterface(numberOfRestaurants);
            }
        });

        frame.setVisible(true);
    }

    private void showRestaurantInfoInterface(int numberOfRestaurants) {
        frame.getContentPane().removeAll();
        frame.setTitle("Restaurant Information");
        frame.setSize(300, 200);

        restaurantInfos = new ArrayList<>();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        for (int i = 1; i <= numberOfRestaurants; i++) {
            Label nameLabel = new Label("Restaurant " + i + " Name: ");
            TextField nameTextField = new TextField();

            Label capacityLabel = new Label("Restaurant " + i + " Capacity: ");
            TextField capacityTextField = new TextField();

            gridPane.add(nameLabel, 0, i);
            gridPane.add(nameTextField, 1, i);
            gridPane.add(capacityLabel, 2, i);
            gridPane.add(capacityTextField, 3, i);

            restaurantInfos.add(new RestaurantInfo(nameTextField, capacityTextField));
        }

        Button validateButton = new Button("Validate");
        validateButton.setOnAction(event -> createRestaurantAgents());

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        borderPane.setBottom(validateButton);

        JFXPanel fxPanel = new JFXPanel();
        Platform.runLater(() -> {
            fxPanel.setScene(new Scene(borderPane));
        });

        frame.add(fxPanel, BorderLayout.CENTER);
        frame.revalidate();
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
                AgentController agentController = agentContainer.createNewAgent("restaurant" + (i + 1), agents.RestaurantAgent.class.getName(), agentArgs);
                System.out.println("The agent " + restaurantName + " belongs to the container: " + agentContainer.getContainerName());
                agentController.start();
            }

            // Display PersonneContainer after creating agents
            displayPersonneContainer();
        } catch (ControllerException e) {
            e.printStackTrace();
        }

        frame.dispose(); // Close the previous interface
    }

    private void displayPersonneContainer() {
        // Assuming PersonneContainer has a static main method
        PersonneContainer.main(new String[0]);
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
