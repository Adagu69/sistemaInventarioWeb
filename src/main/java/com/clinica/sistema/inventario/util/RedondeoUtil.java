package com.clinica.sistema.inventario.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RedondeoUtil {

    // Redondeo a 1 decimal con HALF_UP (ej: 7.25 → 7.3, 7.74 → 7.7)
    public static double redondear(double valor) {
        BigDecimal bd = BigDecimal.valueOf(valor);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
