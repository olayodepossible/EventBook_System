package com.possible.eventbooking.security;

import com.possible.eventbooking.exception.AccountNotFoundException;
import com.possible.eventbooking.model.User;
import com.possible.eventbooking.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtAuthenticationHelper jwtHelper;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		String username = null;
		String token = null;

		if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
			token = requestHeader.substring(7);
			try{
				username = jwtHelper.extractUsername(token);
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					User userDetails = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
					if (Boolean.FALSE.equals(jwtHelper.isTokenExpired(token))) {
						UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					}
				}
			}
			catch (ExpiredJwtException e){
				log.error("Token Expired");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Expired");
				return;
			}

		}
		filterChain.doFilter(request, response);
	}
}

