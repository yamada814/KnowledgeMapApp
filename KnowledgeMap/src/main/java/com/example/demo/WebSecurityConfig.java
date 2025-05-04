package com.example.demo;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.formLogin(login -> login
				.loginPage("/login")		//ログイン用ページ
				.loginProcessingUrl("/login") // ログインフォームのリクエスト先
				.usernameParameter("username") //認証のキーとなるパラメータ
				.defaultSuccessUrl("/wordbooks",true) //認証成功時 遷移先
				.failureUrl("/login?error=true") //認証失敗時 遷移先
				.permitAll()
			
			).logout(logout -> logout
				.logoutSuccessUrl("/login")
				
			).authorizeHttpRequests(authz -> authz
						.requestMatchers(
								PathRequest.toStaticResources().atCommonLocations()).permitAll() // 静的リソース（CSS, JS, 画像など）は許可
						 .requestMatchers("/login").permitAll()
						 .requestMatchers("/wordbooks").authenticated()
						.anyRequest().authenticated()
					);
		return http.build();
		
	}
	

}
