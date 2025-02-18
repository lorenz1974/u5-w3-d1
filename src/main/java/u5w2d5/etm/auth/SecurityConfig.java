package u5w2d5.etm.auth;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;

/**
 * Configura le impostazioni di sicurezza per l'applicazione.
 *
 * Questa classe imposta la catena di filtri di sicurezza, la gestione delle
 * sessioni e la configurazione CORS. L'autenticazione si basa su JWT e
 * non utilizza sessioni per mantenere lo stato dell'utente.
 *
 * La configurazione include:
 * - Disabilitazione della protezione CSRF (perché JWT è stateless).
 * - Configurazione delle policy CORS per consentire richieste cross-origin.
 * - Permesso di accesso pubblico a URL definiti.
 * - Richiesta di autenticazione per tutti gli altri endpoint.
 * - Configurazione di un entry point personalizzato per gestire errori di
 * autenticazione.
 * - Impostazione di un filtro JWT per intercettare e validare i token prima che
 * la richiesta venga elaborata.
 *
 * Alternativa:
 * - Se si usassero sessioni, si potrebbe configurare lo stato della sessione
 * con `SessionCreationPolicy.IF_REQUIRED`.
 * - Se si volesse mantenere CSRF abilitato, sarebbe necessario un meccanismo di
 * gestione dei token CSRF lato client.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Definisce gli endpoint pubblici accessibili senza autenticazione.
     *
     * @return Lista degli URL accessibili senza autenticazione.
     *
     *         Alternativa:
     *         - Si potrebbe implementare un meccanismo di configurazione per
     *         caricare queste URL da un file di configurazione esterno.
     *         - Si potrebbe proteggere Swagger UI dietro autenticazione per
     *         ambienti di produzione.
     */
    @Bean
    public List<String> publicUrls() {
        return List.of(
                "/api/auth/**", // Endpoint per autenticazione e registrazione
                "/api/employees/**", // Endpoint per gestione dipendenti TEMPORANEOOOO!!!!
                "/public/**", // Endpoint aperti
                "/swagger-ui/**", // Documentazione Swagger
                "/v3/api-docs/**", // API documentation OpenAPI
                "/error",
                "/sw.js"); // Service worker per caching
    }

    /**
     * Configura la catena di filtri di sicurezza per l'applicazione.
     *
     * @param http Oggetto {@link HttpSecurity} da configurare.
     * @return La catena di filtri di sicurezza configurata.
     * @throws Exception se la configurazione fallisce.
     *
     *                   Alternativa:
     *                   - Se si usasse Basic Authentication, si potrebbe usare
     *                   `.httpBasic(Customizer.withDefaults())` al posto del filtro
     *                   JWT.
     *                   - Se si volesse proteggere Swagger UI, si potrebbe
     *                   rimuovere `/swagger-ui/**` dalla lista di URL pubblici.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /**
                 * Disabilita la protezione CSRF.
                 * - JWT è stateless, quindi non è vulnerabile agli attacchi CSRF.
                 * - CSRF è utile per sessioni di autenticazione basate su cookie.
                 */
                .csrf(csrf -> csrf.disable())

                /**
                 * Configura la gestione delle richieste CORS.
                 * - Necessario se l'API è chiamata da domini diversi.
                 */
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                /**
                 * Configura le autorizzazioni per gli endpoint.
                 * - Gli URL definiti in `publicUrls()` sono accessibili a tutti.
                 * - Tutti gli altri endpoint richiedono autenticazione.
                 */
                .authorizeHttpRequests(auth -> {
                    publicUrls().forEach(url -> auth.requestMatchers(url).permitAll());
                    auth.anyRequest().authenticated();
                })

                /**
                 * Configura la gestione delle eccezioni.
                 * - Se un utente non autenticato tenta di accedere a un endpoint protetto,
                 * viene gestito dall'`authenticationEntryPoint`.
                 */
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                /**
                 * Configura la gestione delle sessioni.
                 * - `STATELESS`: Ogni richiesta deve includere il token JWT, senza mantenere
                 * sessioni lato server.
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        /**
         * Aggiunge il filtro per la gestione dei token JWT.
         * - Il filtro intercetta le richieste prima
         * dell'`UsernamePasswordAuthenticationFilter`.
         */
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura le impostazioni CORS per l'applicazione.
     *
     * @return Una configurazione CORS che consente richieste da qualsiasi origine.
     *
     *         Alternativa:
     *         - Per maggiore sicurezza, si potrebbe restringere l'accesso solo ai
     *         domini specifici
     *         (`setAllowedOrigins(List.of("https://example.com"))`).
     *         - Se l'app utilizza credenziali (come cookie per autenticazione
     *         OAuth2), `setAllowCredentials(true)` dovrebbe essere abilitato, ma
     *         con origini specifiche.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // Permette richieste da qualsiasi origine
        configuration.addAllowedMethod("*"); // Permette tutti i metodi HTTP (GET, POST, PUT, DELETE, etc.)
        configuration.addAllowedHeader("*"); // Permette qualsiasi header nella richiesta
        configuration.setAllowCredentials(false); // Per sicurezza, disabilita credenziali se tutte le origini sono
                                                  // permesse

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configura il password encoder per l'applicazione.
     *
     * @return Un'istanza di {@link BCryptPasswordEncoder}.
     *
     *         Alternativa:
     *         - `NoOpPasswordEncoder` potrebbe essere usato per ambienti di test,
     *         ma è insicuro per produzione.
     *         - `PBKDF2`, `SCrypt`, `Argon2` sono alternative moderne con maggiore
     *         resistenza a brute force.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura l'AuthenticationManager.
     *
     * @param authenticationConfiguration Configurazione di autenticazione.
     * @return L'istanza di AuthenticationManager.
     * @throws Exception se la configurazione fallisce.
     *
     *                   Alternativa:
     *                   - Se si volesse supportare più metodi di autenticazione, si
     *                   potrebbero aggiungere provider personalizzati.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
