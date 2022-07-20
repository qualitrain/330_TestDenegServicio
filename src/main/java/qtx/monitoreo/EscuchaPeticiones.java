package qtx.monitoreo;

import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class EscuchaPeticiones
 *
 */
@WebListener
public class EscuchaPeticiones implements ServletRequestListener {

    /**
     * Default constructor. 
     */
    public EscuchaPeticiones() {
    }

	/**
     * @see ServletRequestListener#requestDestroyed(ServletRequestEvent)
     */
    public void requestDestroyed(ServletRequestEvent sre)  { 
    }

	/**
     * @see ServletRequestListener#requestInitialized(ServletRequestEvent)
     */
    public void requestInitialized(ServletRequestEvent sre)  { 
    	ServletRequest req = sre.getServletRequest();
    	req.setAttribute("fechaNacimiento", new Date());
    }
	
}
