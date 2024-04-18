package agents;

import container.RestaurantContainer;
import jade.core.Agent;
import jade.core.MessageQueue;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.OutgoingEncodingFilter;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

public class PersonneAgent extends Agent {

    private int nombrePersonnes;

    protected void setup() {
        Object[] args = getArguments();
        if (args[0] instanceof Integer) {
            nombrePersonnes = (int) args[0];

            System.out.println("Agent " + getLocalName() + " created. Nombre de personnes: " + nombrePersonnes);
        } else {
            System.err.println("Invalid arguments. Expected an Integer and a List.");
            doDelete();
        }

        addBehaviour(new ReservationBehaviour());
    }

    private class ReservationBehaviour extends CyclicBehaviour {
        public void action() {
            int random = (int) (Math.random() * RestaurantContainer.numberOfRestaurants) + 1;
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent("restaurant : " + random + ", nombrePersonnes :" + nombrePersonnes);
            aclMessage.addReceiver(RestaurantContainer.getRestaurantAgent(random));
            send(aclMessage);
        }
    }
}
