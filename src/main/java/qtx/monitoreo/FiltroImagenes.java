package qtx.monitoreo;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebFilter("/img/*")
public class FiltroImagenes implements Filter {
	private String imagenCensurada;
	private String imagenReemplazo;

    public FiltroImagenes() {
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest peticion = (HttpServletRequest) request;
		HttpServletResponse respuesta = (HttpServletResponse) response;
		String uri = peticion.getServletPath();
		
		System.out.println("\tFiltroImagenes(PRE-PROCESAMIENTO): " + peticion.getMethod() + " " 
		                    + peticion.getServletPath() + " Hilo " + Thread.currentThread().getId());
		
		if(peticion.getMethod().equalsIgnoreCase("GET") == false) {
			respuesta.setStatus(501);
		}
		else
		if(uri.equals(imagenCensurada)){
			System.out.println("\t\tFiltroImagenes: Cambiando "+imagenCensurada
												+" por "+ imagenReemplazo 
												+ " Hilo " + Thread.currentThread().getId());
			respuesta.sendRedirect(peticion.getServletContext()
					                       .getContextPath()+imagenReemplazo);
		}
		else{
			chain.doFilter(request, response);
			System.out.println("\tFiltroImagenes(POST-PROCESAMIENTO) " + " Hilo " + Thread.currentThread().getId());
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		imagenCensurada = "/img/gato.gif";
		imagenReemplazo = "/img/censura.jpg";
	}

}
