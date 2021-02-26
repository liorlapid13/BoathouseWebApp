package webapp.utils;

import engine.Engine;
import engine.chat.ChatManager;

import javax.servlet.ServletContext;

import static webapp.constants.Constants.CHAT_MANAGER_ATTRIBUTE_NAME;
import static webapp.constants.Constants.ENGINE_ATTRIBUTE_NAME;

public class ServletUtils {
    public static Engine getEngine(ServletContext servletContext) {
        return (Engine)servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }
}
