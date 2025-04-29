package com.clinica.sistema.inventario.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Captura excepciones de registro de usuario
    @ExceptionHandler(RegistroException.class)
    public String handleRegistroException(RegistroException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "registro"; // Página de registro (u otra donde quieras enviar el error)
    }

    // Captura otras excepciones generales
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        model.addAttribute("error", "Ocurrió un error inesperado: " + ex.getMessage());
        return "error-general"; // Crea una página "error-general.html"
    }
}
