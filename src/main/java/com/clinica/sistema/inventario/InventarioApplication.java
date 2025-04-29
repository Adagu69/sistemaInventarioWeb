package com.clinica.sistema.inventario;

import com.clinica.sistema.inventario.model.Rol;
import com.clinica.sistema.inventario.repository.RolRepositorio;
import org.springframework.boot.CommandLineRunner;
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

	@Bean
	CommandLineRunner initRoles(RolRepositorio rolRepo) {
		return args -> {
			if (rolRepo.findByNombre("ROLE_USER").isEmpty())
				rolRepo.save(new Rol("ROLE_USER"));
			if (rolRepo.findByNombre("ROLE_ADMIN").isEmpty())
				rolRepo.save(new Rol("ROLE_ADMIN"));
		};
	}




}
