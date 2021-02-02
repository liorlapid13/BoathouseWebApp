package webapp.utils;

import engine.Engine;

import javax.servlet.ServletContext;

import static webapp.constants.Constants.ENGINE_ATTRIBUTE_NAME;

public class ServletUtils {
    public static Engine getEngine(ServletContext servletContext) {
        return (Engine)servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME);
    }
}
