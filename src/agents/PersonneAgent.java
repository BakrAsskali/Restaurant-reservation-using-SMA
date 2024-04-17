package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage message = receive(mt);
            if (message != null) {
                String content = message.getContent();
                System.out.println(content);
            } else {
                block();
            }
        }
    }
}
