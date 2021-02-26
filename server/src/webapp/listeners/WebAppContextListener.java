package webapp.listeners;

import engine.Engine;
import engine.chat.ChatManager;
import engine.member.Member;
import engine.member.MemberLevel;
import webapp.constants.Constants;
import webapp.utils.ServerUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.time.LocalDateTime;

@WebListener
public class WebAppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        File xmlFile = new File("system_state.xml");
        boolean isFirstRun = !xmlFile.exists();
        Engine engine;

        try {
            if (!isFirstRun) {
                JAXBContext jaxbContext = JAXBContext.newInstance(Engine.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                engine = (Engine)jaxbUnmarshaller.unmarshal(xmlFile);
                engine.createXmlHandler();
                engine.linkSystemObjectReferences();
            } else {
                engine = new Engine();
                engine.createXmlHandler();
                Member firstRunManager = new Member("1","admin",27,null, MemberLevel.ADVANCED,
                        false,null,"0",
                        "admin@bms.com", "123456", true,
                        LocalDateTime.now(), LocalDateTime.now().plusYears(1));
                engine.addMemberToList(firstRunManager);
            }

            servletContext.setAttribute(Constants.ENGINE_ATTRIBUTE_NAME, engine);
            servletContext.setAttribute(Constants.CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
            if (isFirstRun) {
                ServerUtils.saveSystemState(servletContext);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
