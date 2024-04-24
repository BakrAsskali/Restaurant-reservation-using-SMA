package agents;

import container.PersonneContainer;
import container.RestaurantContainer;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.beans.property.SimpleStringProperty;

public class PersonneAgent extends Agent {
    private int nombrePersonnes;
    private int messageCount = 0;

    protected void setup() {
        nombrePersonnes = (int) (Math.random() * 10) + 1;

        addBehaviour(new ReservationBehaviour(this));
        addBehaviour(new ReceiveResponses());
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public int getMessageCount() {
        return messageCount;
    }

    private class ReservationBehaviour extends CyclicBehaviour {
        private final PersonneAgent agent;

        public ReservationBehaviour(PersonneAgent agent) {
            this.agent = agent;
        }

        public void action() {
            if (agent.getMessageCount() < RestaurantContainer.numberOfRestaurants) {
                int random = (int) (Math.random() * RestaurantContainer.numberOfRestaurants) + 1;
                ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                aclMessage.setContent("restaurant : " + random + ", numberPersons :" + agent.getNombrePersonnes());
                aclMessage.addReceiver(RestaurantContainer.getRestaurantAgent(random));
                System.out.println(RestaurantContainer.getRestaurantAgent(random));
                System.out.println("Agent " + myAgent.getLocalName() + " sent a request to restaurant " + random);
                myAgent.send(aclMessage);
                agent.messageCount++;
            } else {
                // If all messages are sent, stop the behavior
                System.out.println("Agent " + myAgent.getLocalName() + " has sent all reservation requests.");
                myAgent.removeBehaviour(this);
            }
        }
    }

    private class ReceiveResponses extends CyclicBehaviour {
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage message = receive(messageTemplate);
            if (message != null) {
                String content = message.getContent();
                System.out.println("Agent " + myAgent.getLocalName() + " received a response: " + content);
                PersonneContainer.message = content;
                PersonneContainer.nameColumn.setCellValueFactory(_ -> {
                    try {
                        return new SimpleStringProperty(content);
                    } catch (Exception e) {
                        return new SimpleStringProperty("Error");
                    }
                });

                // Remove the ReservationBehaviour
                myAgent.removeBehaviour(new ReservationBehaviour((PersonneAgent) myAgent));
            } else {
                block();
            }
        }
    }
}
