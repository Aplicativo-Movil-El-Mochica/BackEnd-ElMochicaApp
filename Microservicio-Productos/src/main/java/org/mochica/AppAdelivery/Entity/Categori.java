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


    public static Categori fromFirestoreValue(String firestoreValue) {
        String formattedValue = firestoreValue.replace(" ", "_").toUpperCase();
        try {
            return Categori.valueOf(formattedValue);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Valor de categoría no válido en Firestore: " + firestoreValue);
        }
    }
}