package mx.com.qtx.tdneg.web;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Test")
public class MiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int PAUSA_MAX_MILIS = 2000;
	
	private static long nPeticion;
	private Map<Long,Integer> peticionesXhilo = new ConcurrentHashMap<>();
	private Object lock = new Object();
       
    public MiServlet() {
        super();
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		synchronized(lock) {
			nPeticion++;
		}   	
		long idHilo = Thread.currentThread().getId();
		actualizarPeticionesXhilo(idHilo);
		
		mostrarPeticionEHiloEjecutorServlet(req, idHilo);
     	super.service(req, resp);
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String mediaTypeSolicitado = getMediaTypeRequerido(request);
		
		switch(mediaTypeSolicitado) {
			case "application/json"->{ 
				generarRespuestaJSon(request, response);
			}
			case "text/plain" ->{
				generarRespuestaTextoPlano(request, response);
			}
			case "text/html" ->{
				generarRespuestaHtml(request, response);
			}
			default->{
				generarRespuestaMediaTypeNoSoportado(response, mediaTypeSolicitado);
			}
		}
		
		hacerPausaAleatoria();
	}

	private void generarRespuestaMediaTypeNoSoportado(HttpServletResponse response, String mediaTypeSolicitado)
			throws IOException {
		response.getWriter()
		.append("MediaType no soportado:" + mediaTypeSolicitado);
	}

	private void generarRespuestaHtml(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.getWriter()
				.append("<!DOCTYPE html><html><head>"
						+ "<meta charset=\"ISO-8859-1\"><title>Mensaje</title>"
						+ "</head><body>")
				.append("<p>Respondiendo a una petición GET: ")
		        .append(request.getContextPath() + " " + nPeticion + "</p>")
				.append("</body></html>");
	}

	private void generarRespuestaTextoPlano(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/plain");
		response.getWriter()
				.append("Respondiendo a una petición GET: ")
		        .append(request.getContextPath() + " " + nPeticion);
	}

	private void generarRespuestaJSon(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.getWriter()
		        .append("{\"texto\":"
		        		+ "Respondiendo a una petición GET: "
		        		+ request.getContextPath()
		        		+ "}");
	}

	private String getMediaTypeRequerido(HttpServletRequest request) {
		String cteAceptaMediaType = request.getHeader("accept");
		if(cteAceptaMediaType.contains(",")) {
			String[] arrMediaType = cteAceptaMediaType.split(",");
			cteAceptaMediaType = arrMediaType[0];
		}
		return cteAceptaMediaType;
	}

	private void mostrarPeticionEHiloEjecutorServlet(HttpServletRequest request, long idHilo) {
		String queryString = request.getQueryString() == null ? "" : ("?" + request.getQueryString());
		System.out.print("\t\tMiServelt.do"
				+ capitalizar(request.getMethod())
				+ ": "
				+ request.getMethod()
				+ " " + request.getServletPath() + queryString);
		System.out.println(" ---> Hilo en Servidor:" + idHilo 
							+ ", activeCount (hilos activos):" + Thread.activeCount());
		
//		mostrarCabecerasHttp(request);
		
		Date fechaNac = (Date) request.getAttribute("fechaNacimiento");
		System.out.println("\t\t- Esta petición [" + nPeticion
				           + "] nació " + fechaNac);
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
		int milis = (int) (long)(Math.random()*17137) % PAUSA_MAX_MILIS;
		System.out.println("\t\t- Pausa aleatoria de " + milis + " milisegundos");
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

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter()
        .append("Respondiendo a una petición POST: ")
        .append(request.getContextPath());
	}

    @Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter()
        .append("Respondiendo a una petición PUT: ")
        .append(request.getContextPath());
	}

    @Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter()
        .append("Respondiendo a una petición DELETE: ")
        .append(request.getContextPath());
	}
    
	@Override
	public void destroy() {
		emitirEstadisticasOperativasImpresas();
		super.destroy();
	}

	private void emitirEstadisticasOperativas() {
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
	}
	
	private void emitirEstadisticasOperativasImpresas() {
		String nomArchivo = getNomArchivo();
		try (PrintWriter pw = new PrintWriter(new FileWriter(nomArchivo))){
			pw.println("\n-----------------------------------------------------------------------------------------------");
			pw.println(this.getServletName());
			pw.println("Hilos creados :" + this.peticionesXhilo.size());
			int totPeticiones = 0;
			for(Long idHiloI : this.peticionesXhilo.keySet()) {
				int nPeticiones = this.peticionesXhilo.get(idHiloI);
				totPeticiones += nPeticiones;
				pw.println("Hilo:" + idHiloI + " atendió "+ nPeticiones );
			}
			pw.println("Peticiones atendidas :" + totPeticiones);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String getNomArchivo() {
		String rutaTemporales = System.getenv("TEMP");
		
		LocalDateTime ahora = LocalDateTime.now();
	    String nomArchivo = rutaTemporales + "\\" 
	    							+ this.getServletContext()
	    							       .getContextPath()
	    							       .replace("/", "") + "_"
		                            + this.getClass().getSimpleName() + "_" 
									+ ahora.getYear() 
									+ ahora.getMonthValue()
									+ ahora.getDayOfMonth() 
									+ ahora.getHour()
									+ ahora.getMinute() 
									+ ahora.getSecond()
									+ ".txt";
	    System.out.println("nomArchivo:" + nomArchivo);
	    return nomArchivo;
	}
	
	public static String capitalizar(String cad) {
	    if (cad == null || cad.isEmpty()) {
	        return cad; // Retorna null o cadena vacía según el input
	    }
	    // Primera letra en mayúscula + resto en minúscula
	    return cad.substring(0, 1).toUpperCase() + cad.substring(1).toLowerCase();
	}

}
