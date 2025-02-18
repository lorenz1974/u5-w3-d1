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
 * Configures the security settings for the application.
 *
 * This class sets up the security filter chain, CORS configuration, and other
 * security-related beans. It uses JWT for stateless authentication and
 * configures public URLs that do not require authentication.
 *
 * The security configuration includes:
 * - Disabling CSRF protection to allow stateless authentication using JWT.
 * - Configuring CORS settings to allow cross-origin requests.
 * - Permitting unauthenticated access to specified public URLs.
 * - Requiring authentication for any other request.
 * - Setting a custom authentication entry point for handling authentication
 * exceptions.
 * - Configuring session management to be stateless.
 * - Adding a custom JWT filter to validate JWT tokens before processing
 * authentication.
 *
 * The CORS configuration includes:
 * - Allowing all origins (`configuration.addAllowedOrigin("*")`).
 * - Allowing all HTTP methods (`configuration.addAllowedMethod("*")`).
 * - Allowing all headers (`configuration.addAllowedHeader("*")`).
 * - Not allowing the sending of credentials
 * (`configuration.setAllowCredentials(false)`).
 *
 * Theoretical concepts:
 * - CSRF (Cross-Site Request Forgery): A type of attack that tricks the user
 * into performing actions they did not intend to perform.
 * - CORS (Cross-Origin Resource Sharing): A security mechanism that allows
 * servers to specify which domains can access their resources.
 * - JWT (JSON Web Token): A compact, URL-safe means of representing claims to
 * be transferred between two parties.
 * - Stateless Authentication: An authentication mechanism where the server does
 * not maintain any state about the user between requests.
 * - Authentication Entry Point: A component that handles authentication
 * exceptions, such as unauthorized access.
 * - Session Management: The process of managing user sessions, which can be
 * stateful or stateless.
 *
 * Note:
 * - `setAllowCredentials(false)` is set to prevent the sending of credentials
 * (such as cookies) with cross-origin requests when `allowedOrigins` is set to
 * "*". This is a security measure to prevent unauthorized access to resources.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public List<String> publicUrls() {
        return List.of(
                "/api/auth/**", // Endpoint di autenticazione
                "/public/**", // Endpoint pubblici
                "/swagger-ui/**", // Documentazione Swagger
                "/v3/api-docs/**", // Documentazione Swagger
                "/error",
                "/sw.js");
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the {@link SecurityFilterChain} that was built
     * @throws Exception if an error occurs
     *
     *                   Note:
     *                   - {@link HttpSecurity#csrf(java.util.function.Consumer)}:
     *                   Disables Cross-Site Request Forgery (CSRF) protection.
     *                   - {@link HttpSecurity#cors(java.util.function.Consumer)}:
     *                   Configures Cross-Origin Resource Sharing (CORS) settings.
     *                   -
     *                   {@link HttpSecurity#authorizeHttpRequests(java.util.function.Consumer)}:
     *                   Configures authorization for HTTP requests.
     *                   -
     *                   {@link HttpSecurity#exceptionHandling(java.util.function.Consumer)}:
     *                   Configures exception handling for authentication.
     *                   - {@link HttpSecurity#build()}: Builds the
     *                   {@link SecurityFilterChain}.
     *
     *                   Details:
     *                   - Disables CSRF protection using
     *                   {@link HttpSecurity#csrf(java.util.function.Consumer)}:
     *                   CSRF protection is disabled to allow stateless
     *                   authentication using JWT.
     *                   - Configures CORS settings using
     *                   {@link HttpSecurity#cors(java.util.function.Consumer)}:
     *                   Configures CORS to allow cross-origin requests from
     *                   specified origins.
     *                   - Permits all requests to URLs specified in
     *                   {@code publicUrls} using
     *                   {@link HttpSecurity#authorizeHttpRequests(java.util.function.Consumer)}:
     *                   Allows unauthenticated access to the URLs specified in the
     *                   publicUrls list.
     *                   - Requires authentication for any other request using
     *                   {@link HttpSecurity#authorizeHttpRequests(java.util.function.Consumer)}:
     *                   Ensures that any request not specified in publicUrls
     *                   requires authentication.
     *                   - Sets a custom authentication entry point for handling
     *                   authentication exceptions using
     *                   {@link HttpSecurity#exceptionHandling(java.util.function.Consumer)}:
     *                   Configures a custom entry point to handle authentication
     *                   exceptions, such as unauthorized access.
     *                   - Configures session management using
     *                   {@link HttpSecurity#sessionManagement(java.util.function.Consumer)}:
     *                   Sets the session management policy to stateless, as JWT is
     *                   used for authentication.
     *                   - Adds a custom JWT filter before the
     *                   {@link UsernamePasswordAuthenticationFilter} using
     *                   {@link HttpSecurity#addFilterBefore(javax.servlet.Filter, Class)}:
     *                   Adds the JwtRequestFilter to validate JWT tokens before
     *                   processing authentication.
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests -> {
                    publicUrls().forEach(url -> authorizeRequests.requestMatchers(url).permitAll());
                    authorizeRequests.anyRequest().authenticated();
                })
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura le impostazioni CORS (Cross-Origin Resource Sharing) per
     * l'applicazione.
     *
     * CORS è un meccanismo che permette di controllare quali risorse possono essere
     * richieste da un dominio diverso
     * rispetto a quello da cui il server ha ricevuto la richiesta. Questa
     * configurazione è utile per abilitare o
     * limitare l'accesso alle risorse del server da parte di client web che
     * risiedono su domini differenti.
     *
     * @return CorsConfigurationSource che contiene le impostazioni CORS
     *         configurate.
     *
     *         La configurazione include:
     *         - Permettere tutte le origini (configuration.addAllowedOrigin("*")).
     *         - Permettere tutti i metodi HTTP
     *         (configuration.addAllowedMethod("*")).
     *         - Permettere tutti gli header (configuration.addAllowedHeader("*")).
     *         - Non consentire l'invio di credenziali
     *         (configuration.setAllowCredentials(false)).
     *
     *         La configurazione viene applicata a tutti i percorsi ("/**").
     *
     *         Concetti teorici:
     *         - CORS (Cross-Origin Resource Sharing): È un meccanismo di sicurezza
     *         che permette ai server di controllare
     *         quali risorse possono essere richieste da domini diversi da quello da
     *         cui il server ha ricevuto la richiesta.
     *         Questo è importante per prevenire attacchi come Cross-Site Scripting
     *         (XSS) e Cross-Site Request Forgery (CSRF).
     *         - Origini: Si riferisce ai domini da cui possono provenire le
     *         richieste. Permettere tutte le origini significa
     *         che qualsiasi dominio può fare richieste al server.
     *         - Metodi HTTP: Si riferisce ai tipi di richieste HTTP (GET, POST,
     *         PUT, DELETE, ecc.) che sono permessi.
     *         - Header: Si riferisce agli header HTTP che possono essere inclusi
     *         nelle richieste.
     *         - Credenziali: Si riferisce all'invio di cookie o altre credenziali
     *         di autenticazione con le richieste.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // Permetti tutte le origini
        configuration.addAllowedMethod("*"); // Permetti tutti i metodi HTTP
        configuration.addAllowedHeader("*"); // Permetti tutti gli header
        configuration.setAllowCredentials(false); // Consente l'invio di credenziali, mettere false se allowedOrigins è
                                                  // "*"

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}