package com.product_order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.product_order.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

//	@Bean
//	SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
//		http.csrf(csrf -> csrf.disable())
//				.authorizeHttpRequests(auth -> auth
//						.requestMatchers("/", "/login", "/register", "/do-register", "/css/**", "/Service").permitAll()
//						.requestMatchers("/superadmin/**").hasRole("SUPER_ADMIN").requestMatchers("/admin/**")
//						.hasAnyRole("ADMIN", "SUPER_ADMIN").requestMatchers("/user/**")
//						.hasAnyRole("USER", "ADMIN", "SUPER_ADMIN").anyRequest().authenticated())
//				.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/home", true).permitAll())
//				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"));
//		return http.build();
//	}

	@Bean
	SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/api/auth/me").authenticated().requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/superadmin/**").hasRole("SUPER_ADMIN").requestMatchers("/api/admin/**")
						.hasAnyRole("ADMIN", "SUPER_ADMIN").requestMatchers("/api/user/**")
						.hasAnyRole("USER", "ADMIN", "SUPER_ADMIN").anyRequest().authenticated())
				.formLogin(form -> form.loginProcessingUrl("/api/auth/login")
						.successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
						.failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
						.permitAll())
				.logout(logout -> logout.logoutUrl("/api/auth/logout")
						.logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)))
				.exceptionHandling(
						ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
		return http.build();
	}

}