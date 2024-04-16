package agents;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;

public class RestaurantAgent extends Agent {
    private int id;
    private String nomRestaurant;
    private int RestaurantCapacity;


    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            if (args[0] instanceof String && args[1] instanceof Integer) {
                nomRestaurant = (String) args[0];
                RestaurantCapacity = (Integer) args[1];

                System.out.println("Agent " + getLocalName() + " created. Nom: " + nomRestaurant + ", Capacity: " + RestaurantCapacity);
            } else {
                System.err.println("Invalid arguments. Expected a String and an Integer.");
                doDelete();
            }
        } else {
            System.err.println("Invalid number of arguments. Expected 2 arguments.");
            doDelete();
        }
    }

    protected void takeDown() {
        System.out.println("Agent " + getLocalName() + " terminating.");
    }
}