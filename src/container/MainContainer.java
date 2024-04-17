package container;

import jade.core.*;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;

public class MainContainer {
    public static void main(String[] args) throws Exception{
        Runtime runtime=Runtime.instance();
        Properties properties = new ExtendedProperties();

        ProfileImpl profile=new ProfileImpl();
        properties.setProperty(Profile.GUI, "true");
        ProfileImpl profileImpl = new ProfileImpl(properties);
        AgentContainer mainContainer = runtime.createMainContainer(profileImpl);

        mainContainer.start();


    }
}
