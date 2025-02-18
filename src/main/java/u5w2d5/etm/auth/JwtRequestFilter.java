package u5w2d5.etm.auth;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro JWT per intercettare e validare i token nelle richieste HTTP.
 *
 * Questa classe estende {@link OncePerRequestFilter}, il che significa che il
 * filtro
 * viene eseguito una volta per ogni richiesta HTTP. Il suo scopo è:
 * - Estrarre il token JWT dall'header "Authorization".
 * - Validare il token e verificarne l'integrità.
 * - Recuperare il nome utente dal token e verificare se l'utente è già
 * autenticato.
 * - Se l'utente non è autenticato e il token è valido, impostare manualmente il
 * contesto di autenticazione di Spring Security.
 *
 * Il token JWT deve essere passato nel formato:
 * ```
 * Authorization: Bearer <token>
 * ```
 *
 * **Motivazioni della scelta:**
 * - `OncePerRequestFilter` è preferito rispetto ad altri filtri perché
 * garantisce che il filtro venga eseguito solo una volta per richiesta.
 * - Non viene effettuata alcuna gestione della sessione poiché JWT è stateless.
 * - Il filtro viene eseguito **prima** del
 * `UsernamePasswordAuthenticationFilter` per intercettare e autenticare gli
 * utenti prima che Spring Security verifichi le credenziali.
 *
 * **Alternative:**
 * - Si potrebbe usare `BasicAuthenticationFilter` se si volesse gestire
 * un'autenticazione basata su sessioni.
 * - Se il token JWT venisse trasmesso come cookie anziché nell'header, si
 * dovrebbe estrarlo da `request.getCookies()`.
 *
 * @see OncePerRequestFilter
 * @see UsernamePasswordAuthenticationToken
 * @see SecurityContextHolder
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Intercetta le richieste HTTP per estrarre e validare il token JWT.
     *
     * @param request  La richiesta HTTP in ingresso.
     * @param response La risposta HTTP in uscita.
     * @param chain    La catena di filtri.
     * @throws ServletException In caso di errore durante il filtraggio.
     * @throws IOException      Se si verifica un errore di I/O.
     *
     *                          **Logica di elaborazione:**
     *                          1. Legge l'header "Authorization" dalla richiesta
     *                          HTTP.
     *                          2. Se l'header è presente e inizia con "Bearer ",
     *                          estrae il token JWT.
     *                          3. Tenta di ottenere il nome utente dal token.
     *                          4. Se il token è valido e l'utente non è già
     *                          autenticato, carica i dettagli dell'utente e imposta
     *                          il contesto di sicurezza.
     *                          5. Prosegue la catena di filtri.
     *
     *                          **Alternative:**
     *                          - Se il token non fosse nel formato "Bearer ", si
     *                          potrebbe accettare anche un formato JSON nel corpo
     *                          della richiesta.
     *                          - Se l'API dovesse supportare più metodi di
     *                          autenticazione, si potrebbe implementare un sistema
     *                          di fallback.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // Controlla che l'header Authorization sia presente e nel formato corretto
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7); // Rimuove il prefisso "Bearer "
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("Errore durante l'estrazione del token JWT", e);
                throw new ServletException("Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                log.warn("Token JWT scaduto", e);
                throw new ServletException("JWT Token has expired", e);
            } catch (SecurityException e) {
                log.error("Errore di sicurezza nella validazione del JWT", e);
                throw new ServletException("JWT Token security validation failed", e);
            }
        } else {
            log.warn("JWT Token non presente o non inizia con 'Bearer '");
            chain.doFilter(request, response);
            return;
        }

        // Se il token è valido e l'utente non è già autenticato, procedi con
        // l'autenticazione
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Verifica se il token JWT è valido rispetto ai dettagli dell'utente recuperato
            // dal database o da un sistema di autenticazione esterno.
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                // Crea un oggetto UsernamePasswordAuthenticationToken, che rappresenta
                // un'istanza di autenticazione per Spring Security.
                // - `userDetails`: Oggetto che contiene le informazioni dell'utente
                // autenticato.
                // - `null`: Poiché l'autenticazione avviene tramite JWT, non è necessario un
                // oggetto password.
                // - `userDetails.getAuthorities()`: Le autorizzazioni dell'utente vengono
                // estratte per gestire i permessi.
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Aggiunge i dettagli della richiesta HTTP all'autenticazione.
                // Questo è utile per conservare metadati sulla richiesta originale, come
                // l'indirizzo IP e il tipo di dispositivo.
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Imposta il contesto di autenticazione di Spring Security.
                // - Questo passaggio è fondamentale perché Spring Security utilizza
                // `SecurityContextHolder` per mantenere l'utente autenticato.
                // - Senza questa assegnazione, la richiesta verrebbe trattata come anonima
                // anche se il token JWT è valido.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // Logga l'evento di autenticazione riuscita con il nome utente.
                // Questo aiuta nel debugging e nel monitoraggio della sicurezza.
                log.info("Autenticazione impostata per utente: {}", username);

            } else {
                // Logga un avviso nel caso in cui il token JWT non sia valido per l'utente
                // specificato.
                // Questo può essere utile per rilevare tentativi di accesso con token scaduti o
                // manipolati.
                log.warn("Token JWT non valido per utente: {}", username);
            }

        }
        // Prosegue la catena dei filtri di Spring Security.
        // - Se il token JWT è valido e l'utente è stato autenticato, la richiesta
        // continua con il contesto di sicurezza impostato.
        // - Se il token non è presente o non è valido, la richiesta viene comunque
        // inoltrata agli altri filtri,
        // che possono gestirla in base alle policy di sicurezza configurate (ad
        // esempio, restituendo un errore 401 o permettendo accesso a endpoint
        // pubblici).
        // - Questo metodo è essenziale per garantire che gli altri filtri nella catena
        // (come quelli di Spring Security o altri middleware)
        // possano eseguire la loro logica senza interruzioni.
        //
        // **Come definire il filtro successivo nella catena:**
        // - I filtri vengono eseguiti nell'ordine in cui sono stati registrati nella
        // `SecurityFilterChain`.
        // - Questo filtro (`JwtRequestFilter`) viene tipicamente registrato **prima**
        // del `UsernamePasswordAuthenticationFilter` per intercettare la richiesta
        // e verificare il JWT prima che Spring Security esegua altri controlli di
        // autenticazione.
        //
        // **Esempio di definizione del filtro in una configurazione Spring Security:**
        // ```java
        // http.addFilterBefore(jwtRequestFilter,
        // UsernamePasswordAuthenticationFilter.class);
        // ```
        // - `addFilterBefore(jwtRequestFilter,
        // UsernamePasswordAuthenticationFilter.class)` → Inserisce il filtro JWT prima
        // di quello standard di autenticazione.
        // - È possibile usare `addFilterAfter()` o `addFilterAt()` se si vuole eseguire
        // il filtro in una posizione diversa.
        //
        // **Alternative:**
        // - Se il filtro JWT dovesse essere l'ultimo nella catena, si potrebbe
        // registrarlo con `addFilterAfter(jwtRequestFilter, SomeOtherFilter.class)`.
        // - Se si volesse sostituire completamente un filtro esistente, si potrebbe
        // usare `http.addFilterAt(jwtRequestFilter, ExistingFilter.class)`.
        //
        // **Effetti della mancata chiamata a `chain.doFilter(request, response)`**
        // - Se `chain.doFilter(request, response)` non venisse chiamato, l'esecuzione
        // si interromperebbe e la richiesta non raggiungerebbe il controller o gli
        // altri filtri.
        // - Questo sarebbe utile solo se si volesse bloccare esplicitamente la
        // richiesta in caso di errore (ad esempio, restituendo
        // `response.sendError(HttpServletResponse.SC_UNAUTHORIZED)`).
        chain.doFilter(request, response);

    }
}
