package com.clinica.sistema.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.faces.webapp.FacesServlet;

@SpringBootApplication
public class InventarioApplication {

	public static void main(String[] args) {

		SpringApplication.run(InventarioApplication.class, args);
	}


}
