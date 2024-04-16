package agents;


import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

public class PersonneAgent extends Agent {

    private static final long serialVersionUID = 1L;
    private int nombrePersonnes;
    private List<String> restaurantNames;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length == 2) {
            if (args[0] instanceof String && args[1] instanceof List) {
                nombrePersonnes = Integer.parseInt((String) args[0]);
                restaurantNames = (List<String>) args[1];

                System.out.println("Agent " + getLocalName() + " created. Nombre de personnes: " + nombrePersonnes);
                System.out.println("Liste des restaurants: " + restaurantNames);
            } else {
                System.err.println("Invalid arguments. Expected a String and a List.");
                doDelete();
            }
        } else {
            System.err.println("Invalid number of arguments. Expected 2 arguments.");
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

