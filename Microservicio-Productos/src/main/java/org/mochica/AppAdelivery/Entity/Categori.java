package org.mochica.AppAdelivery.Entity;

public enum Categori {
    ENTRADAS,
    ENSALADAS,
    CEVICHES,
    CAUSAS,
    PESCADOS_Y_MARISCOS,
    CRIOLLOS,
    PIQUEOS,
    OTRAS_ESPECIALIDADES,
    RISOTTOS,
    FETUCCINIS,
    TACU_TACUS,
    PLATOS_CRIOLLOS,
    SOPAS,
    POSTRES;


    public String getFormattedName() {
        // Reemplaza los guiones bajos por espacios y capitaliza cada palabra
        String name = this.name().replace("_", " ").toLowerCase();
        String[] words = name.split(" ");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            formattedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return formattedName.toString().trim(); // Elimina el espacio extra al final
    }
}