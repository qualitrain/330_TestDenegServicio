package mx.com.qtx.tdneg.web.monitoreo;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
