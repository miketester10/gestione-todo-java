package com.example.dataware.todolist.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.dataware.todolist.exception.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j // Logger
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    /**
     * Non eseguire il filtro su endpoint pubblici.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {

                Long userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);

                JwtPayload jwtPayload = JwtPayload.builder()
                        .userId(userId)
                        .email(email)
                        .build();

                /*
                 * Creiamo manualmente un oggetto Authentication da inserire nel
                 * SecurityContext.
                 *
                 * - principal (userId, email):
                 * rappresenta l'identità dell'utente autenticato.
                 * In questo caso usiamo lo userId e l'email estratta dal JWT, già validato
                 * (firma, scadenza e integrità del token sono state verificate prima).
                 * N.B. sarà poi accessibile nei controlloer con @AuthenticationPrincipal
                 *
                 * - credentials (null):
                 * non passiamo password o segreti perché l'autenticazione
                 * NON avviene tramite username/password,
                 * ma tramite un JWT già considerato valido.
                 *
                 * - authorities (List.of()):
                 * elenco dei ruoli/permessi dell'utente.
                 * Viene lasciato vuoto perché:
                 * - l'app non usa ruoli
                 * - oppure i ruoli non sono necessari in questo contesto.
                 *
                 * Usando il costruttore con le authorities, Spring Security
                 * considera automaticamente l'Authentication come AUTENTICATA.
                 */
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        jwtPayload, // principal (accessibile nei controller con @AuthenticationPrincipal)
                        null, // credentials
                        List.of() // authorities
                );

                /*
                 * Inseriamo manualmente l'Authentication nel SecurityContext.
                 *
                 * Da questo momento Spring Security considera la request come AUTENTICATA.
                 * Spring NON esegue ulteriori verifiche sul JWT:
                 * - si fida completamente di ciò che viene messo nel SecurityContext
                 *
                 * Per questo motivo questa istruzione DEVE essere eseguita
                 * SOLO dopo aver validato correttamente il token JWT
                 * (firma, scadenza e integrità).
                 */
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Continua la catena dei filtri
                filterChain.doFilter(request, response);
                return;

            } catch (ExpiredJwtException e) {
                log.warn("JWT scaduto: {}", e.getMessage());
                sendError(response, "JWT token expired");
                return;
            } catch (MalformedJwtException | SecurityException e) {
                log.warn("JWT non valido: {}", e.getMessage());
                sendError(response, "Invalid JWT token");
                return;
            } catch (JwtException e) {
                log.warn("Errore JWT generico: {}", e.getMessage());
                sendError(response, "Invalid JWT token");
                return;
            }
        } else {
            sendError(response, "Missing or malformed Authorization header");
            return;
        }
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorResponse errorResponseObj = ErrorResponse.builder()
                .statusCode(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();

        String errorResponseString = objectMapper.writeValueAsString(errorResponseObj);

        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setStatus(status.value());
        response.getWriter().write(errorResponseString);
    }

}
