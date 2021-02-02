package webapp.utils;

import engine.Engine;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.File;

public class ServerUtils {
    public static void saveSystemState(ServletContext servletContext) {
        try {
            Engine engine = ServletUtils.getEngine(servletContext);
            File file = new File("system_state.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Engine.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(engine, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
