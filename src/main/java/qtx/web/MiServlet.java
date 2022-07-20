package qtx.web;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Test")
public class MiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<Long,Integer> peticionesXhilo = new ConcurrentHashMap<>();
       
    public MiServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		mostrarCabecerasHttp(request);
		long idHilo = Thread.currentThread().getId();
		actualizarPeticionesXhilo(idHilo);
		
		System.out.print("MiServelt.doGet: GET " + request.getServletPath() + "?" + request.getQueryString());
		System.out.println(", ---> Hilo en Servidor:" + idHilo 
							+ ", activeCount:" + Thread.activeCount());
		
		String cteAceptaMediaType = request.getHeader("accept");
		switch(cteAceptaMediaType) {
		case "application/json": 
			response.setContentType("application/json");
			response.getWriter()
			        .append("{\"texto\":"
			        		+ "Respondiendo a una petición GET: "
			        		+ request.getContextPath()
			        		+ "}");
			break;
		case "text/plain":
			response.setContentType("text/plain");
			response.getWriter()
		    		.append("Respondiendo a una petición GET: ")
			        .append(request.getContextPath());
			break;
		case "text/html":
			response.setContentType("text/html");
			response.getWriter()
		    		.append("<!DOCTYPE html><html><head>"
		    				+ "<meta charset=\"ISO-8859-1\"><title>Mensaje</title>"
		    				+ "</head><body>")
		    		.append("<p>Respondiendo a una petición GET: ")
			        .append(request.getContextPath() + "</p>")
					.append("</body></html>");
			
			break;
		default:
			response.getWriter()
    		.append("MediaType no soportado:" + cteAceptaMediaType);

		}
		Date fechaNac = (Date) request.getAttribute("fechaNacimiento");
		System.out.println("Esta petición nació " + fechaNac);
		hacerPausaAleatoria();
	}

	private void actualizarPeticionesXhilo(long idHilo) {
		Integer n = peticionesXhilo.get(idHilo);
		if(n == null) {
			peticionesXhilo.put(idHilo, 1);
		}
		else {
			n++;
			peticionesXhilo.put(idHilo, n);
			
		}
	}

	public void hacerPausaAleatoria(){
		int milis = (int) (long)(Math.random()*17137) % 2000;
		System.out.println("Pausa aleatoria de " + milis * 4);
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void mostrarCabecerasHttp(HttpServletRequest request) {
		System.out.println("-----------------------------------");
		Enumeration<String> cabecerasHttp = request.getHeaderNames();
		while(cabecerasHttp.hasMoreElements()) {
			String nombreCabeceraHttp = cabecerasHttp.nextElement();
			String valorCabeceraHttp = request.getHeader(nombreCabeceraHttp);
			System.out.println(nombreCabeceraHttp + ":" + valorCabeceraHttp);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter()
        .append("Respondiendo a una petición POST: ")
        .append(request.getContextPath());
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter()
        .append("Respondiendo a una petición PUT: ")
        .append(request.getContextPath());
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter()
        .append("Respondiendo a una petición DELETE: ")
        .append(request.getContextPath());
	}
	@Override
	public void destroy() {
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		System.out.println(this.getServletName());
		System.out.println("Hilos creados :" + this.peticionesXhilo.size());
		int totPeticiones = 0;
		for(Long idHiloI : this.peticionesXhilo.keySet()) {
			int nPeticiones = this.peticionesXhilo.get(idHiloI);
			totPeticiones += nPeticiones;
			System.out.println("Hilo:" + idHiloI + " atendió "+ nPeticiones );
		}
		System.out.println("Peticiones atendidas :" + totPeticiones);
		super.destroy();
		
	}
}
