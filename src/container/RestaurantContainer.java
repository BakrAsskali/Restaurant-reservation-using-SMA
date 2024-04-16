package container;

import agents.PersonneAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import javax.swing.*;

import agents.RestaurantAgent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RestaurantContainer {
    private JFrame frame;
    private JTextField textField;
    private static List<RestaurantInfo> restaurantInfos;

    public static void main(String[] args) {
        RestaurantContainer container = new RestaurantContainer();
        container.init();
    }

    private void init() {
        frame = new JFrame("Restaurant Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Number of restaurants:");
        textField = new JTextField(10);
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
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(numberOfRestaurants, 2));

        restaurantInfos = new ArrayList<>();

        for (int i = 1; i <= numberOfRestaurants; i++) {
            JLabel nameLabel = new JLabel("Restaurant " + i + " Name: ");
            JTextField nameTextField = new JTextField(10);

            JLabel capacityLabel = new JLabel("Restaurant " + i + " Capacity: ");
            JTextField capacityTextField = new JTextField(10);

            panel.add(nameLabel);
            panel.add(nameTextField);
            panel.add(capacityLabel);
            panel.add(capacityTextField);

            restaurantInfos.add(new RestaurantInfo(nameTextField, capacityTextField));
        }

        JButton validateButton = new JButton("Validate");
        validateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createRestaurantAgents();

            }
        });

        frame.add(panel, BorderLayout.CENTER);
        frame.add(validateButton, BorderLayout.SOUTH);
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
                AgentController agentController = agentContainer.createNewAgent("restaurant" + (i + 1), RestaurantAgent.class.getName(), agentArgs);
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
        private JTextField nameTextField;
        private JTextField capacityTextField;

        public RestaurantInfo(JTextField nameTextField, JTextField capacityTextField) {
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
