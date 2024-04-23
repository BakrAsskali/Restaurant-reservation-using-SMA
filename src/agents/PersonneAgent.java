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

        addBehaviour(new ReservationBehaviour());
        addBehaviour(new ReceiveResponses());
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

    private class ReceiveResponses extends CyclicBehaviour {
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage message = receive(messageTemplate);
            if (message != null) {
                String content = message.getContent();
                System.out.println("Agent " + getLocalName() + " received a response: " + content);
                PersonneContainer.message = content;
                PersonneContainer.nameColumn.setCellValueFactory(cellData -> {
                    try {
                        return new SimpleStringProperty(message.getContent());
                    } catch (Exception e) {
                        return new SimpleStringProperty("Error");
                    }
                });
            } else {
                block();
            }
        }
    }
}

