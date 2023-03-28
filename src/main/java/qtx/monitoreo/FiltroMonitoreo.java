package qtx.monitoreo;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class FiltroMonitoreo
 */
public class FiltroMonitoreo implements Filter {
	private List<String> peticiones;
    public FiltroMonitoreo() {
    }

	public void destroy() {
		System.out.println("-----------------------------------------------------------------------------------------------");

		System.out.println("FiltroMonitoreo:Se procesaron "
	                       + this.peticiones.size() + " peticiones");
		
		int i=1;
		for(String strPetI:this.peticiones) {
			System.out.println(String.format("%5d ", i++) + strPetI);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest peticion = (HttpServletRequest) request;
		
		System.out.println("\nFiltroMonitoreo: "+ peticion.getMethod() + " " 
		                  + peticion.getServletPath() + "?" + peticion.getQueryString() + " , Accept:" + peticion.getHeaders("Accept").nextElement() 
				          +", Hilo " + Thread.currentThread().getId() + ", "
				          +" Hilos actuales: " + Thread.activeCount());
		
		this.peticiones.add(peticionToString(peticion));
		
		Date inicio = new Date();
		
		chain.doFilter(request, response);
		
		Date fin = new Date();
		
		System.out.println("FiltroMonitoreo: Petición atendida en " + (fin.getTime() - inicio.getTime()) 
				        + " milisegundos" + " Hilo " + Thread.currentThread().getId());
	}

	public void init(FilterConfig fConfig) throws ServletException {
    	this.peticiones = new Vector<>();
	}
	
	private String peticionToString(HttpServletRequest req) {
		String strPeticion = String.format("%6s %-40s", req.getMethod(), req.getRequestURI() );
		if(req.getQueryString() != null) {
			strPeticion += "? " + req.getQueryString();
		}
		
		strPeticion += " ,\t Headers:[" ;
		Enumeration<String> nomsHeaders = req.getHeaderNames();
		while(nomsHeaders.hasMoreElements()) {
			String nomHeaderI = nomsHeaders.nextElement();
			strPeticion += nomHeaderI + ":" + req.getHeader(nomHeaderI);
			if(nomsHeaders.hasMoreElements())
				strPeticion += ", ";
			else {
				strPeticion += "] ";
				break;
			}			
		}
		
		return strPeticion;
	}
}
