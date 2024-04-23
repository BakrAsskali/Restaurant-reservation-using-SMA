package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import javafx.application.Platform;

public class RestaurantAgent extends Agent {
    private int id;
    public static String nomRestaurant;
    private int RestaurantCapacity;
    ParallelBehaviour parallelBehaviour = new ParallelBehaviour();


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
                return; // Return to avoid adding behaviors if arguments are invalid
            }
        } else {
            System.err.println("Invalid number of arguments. Expected 2 arguments.");
            doDelete();
            return; // Return to avoid adding behaviors if arguments are invalid
        }

        // Create and add behaviors
        addBehaviour(new ReceiveRequests());
    }

    private class ReceiveRequests extends CyclicBehaviour {
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = receive(messageTemplate);
            if (message != null) {
                int requestedCapacity = Integer.parseInt(message.getContent().split(":")[2].trim());
                System.out.println(requestedCapacity);
                if (requestedCapacity <= RestaurantCapacity) {
                    // Accept reservation
                    ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                    aclMessage.setContent("Reservation accepted at " + myAgent.getLocalName());
                    System.out.println("Agent " + getLocalName() + " accepted a reservation for " + requestedCapacity + " persons.");
                    aclMessage.addReceiver(message.getSender());
                    send(aclMessage);
                    RestaurantCapacity -= requestedCapacity;
                } else {
                    // Refuse reservation
                    ACLMessage aclMessage = new ACLMessage(ACLMessage.REFUSE);
                    aclMessage.setContent("Reservation refused at " + myAgent.getLocalName() + ". Capacity exceeded.");
                    System.out.println("Agent " + getLocalName() + " refused a reservation for " + requestedCapacity + " persons.");
                    aclMessage.addReceiver(message.getSender());
                    send(aclMessage);
                }
            } else {
                block();
            }
        }
    }


    protected void takeDown() {
        System.out.println("Agent " + getLocalName() + " terminating.");
    }
}