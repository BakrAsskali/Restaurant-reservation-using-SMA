package agents;

import container.RestaurantContainer;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PersonneAgent extends Agent {
    private int nombrePersonnes;
    private int messageCount = 0;

    protected void setup() {
        Object[] args = getArguments();
        if (args[0] instanceof Integer) {
            nombrePersonnes = (int) args[0];
            System.out.println("Agent " + getLocalName() + " created. Nombre de personnes: " + nombrePersonnes);
        } else {
            System.err.println("Invalid arguments. Expected an Integer.");
            doDelete();
        }

        addBehaviour(new ReservationBehaviour());
    }

    private class ReservationBehaviour extends CyclicBehaviour {
        public void action() {
            if (messageCount < RestaurantContainer.numberOfRestaurants) {
                int random = (int) (Math.random() * RestaurantContainer.numberOfRestaurants) + 1;
                ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                aclMessage.setContent("restaurant : " + random + ", numberPersons :" + nombrePersonnes);
                aclMessage.addReceiver(RestaurantContainer.getRestaurantAgent(random));
                System.out.println("Agent " + getLocalName() + " sent a request to restaurant " + random);
                send(aclMessage);
                messageCount++;
            } else {
                // If all messages are sent, stop the behavior
                System.out.println("Agent " + getLocalName() + " has sent all reservation requests.");
                myAgent.removeBehaviour(this);
            }
        }
    }
}

