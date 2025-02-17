/**
 * JwtAuthenticationEntryPoint è una classe che implementa l'interfaccia AuthenticationEntryPoint di Spring Security.
 * Questa classe viene utilizzata per gestire i tentativi di accesso non autorizzati alle risorse protette.
 *
 * Quando un utente tenta di accedere a una risorsa protetta senza essere autenticato, il metodo commence() viene richiamato automaticamente.
 * Questo metodo invia una risposta HTTP con codice di stato 401 (Non Autorizzato) e un messaggio di errore "Unauthorized access".
 *
 * La classe è annotata con @Component, il che significa che viene gestita dal contenitore di Spring e può essere iniettata in altre parti dell'applicazione.
 *
 * @see org.springframework.security.web.AuthenticationEntryPoint
 * @see jakarta.servlet.http.HttpServletRequest
 * @see jakarta.servlet.http.HttpServletResponse
 * @see org.springframework.security.core.AuthenticationException
 */
package u5w2d5.etm.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
    }
}