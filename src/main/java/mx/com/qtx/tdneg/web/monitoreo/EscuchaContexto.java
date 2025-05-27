package mx.com.qtx.tdneg.web.monitoreo;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class EscuchaContexto
 *
 */
public class EscuchaContexto implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public EscuchaContexto() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	 String ctx = sce.getServletContext().getContextPath().replace("/", "");
    	 sce.getServletContext().setAttribute("contexto", ctx);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    }
	
}
