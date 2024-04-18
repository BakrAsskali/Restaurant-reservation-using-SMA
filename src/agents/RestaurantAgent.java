package agents;

import jade.core.AID;
import jade.core.Agent;
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
            }
        } else {
            System.err.println("Invalid number of arguments. Expected 2 arguments.");
            doDelete();
        }
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            public void action() {
                MessageTemplate messageTemplate=MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage message=receive(messageTemplate);
                if(message!=null) {
                    ACLMessage aclMessage=new ACLMessage(ACLMessage.REQUEST);
                    aclMessage.setContent("restaurant : "+myAgent.getLocalName()+",capacite :"+RestaurantCapacity);
                    send(aclMessage);
                }else {
                    block();
                }
                MessageTemplate messageTemplate2=MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage message2=receive(messageTemplate2);
                if(message2!=null) {
                    if(Integer.parseInt(String.valueOf(RestaurantCapacity)) !=0) {
                        int cp=Integer.parseInt(String.valueOf(RestaurantCapacity))-1;
                        System.out.println(cp);

                        Platform.runLater(() -> {
                            RestaurantCapacity= Integer.parseInt(Integer.toString(cp));
                        });
                        ACLMessage aclMessage=new ACLMessage(ACLMessage.PROPOSE);
                        aclMessage.setContent("reservation complete "+myAgent.getAID().getLocalName());
                        //ams va chercher "rma" ds localhost
                        aclMessage.addReceiver(new AID(message2.getContent(),AID.ISLOCALNAME));
                        send(aclMessage);
                    }
                }else {
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        System.out.println("Agent " + getLocalName() + " terminating.");
    }
}