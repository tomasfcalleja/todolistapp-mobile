package com.example.todolistapp.util;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * SHA-256 con salt aleatorio por usuario.
 *
 * El salt evita que dos usuarios con la misma contrasena tengan el mismo hash,
 * que es lo que hace inutil una tabla precalculada. Se guarda junto al hash,
 * separado por dos puntos: "saltEnHex:hashEnHex".
 *
 * Para produccion se usaria bcrypt o PBKDF2, que ademas son lentos a proposito.
 * Para el TP esto alcanza y se defiende.
 */
public final class HashUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TAM_SALT = 16;

    private HashUtil() {
    }

    public static String hash(String password) {
        byte[] salt = new byte[TAM_SALT];
        RANDOM.nextBytes(salt);
        return aHex(salt) + ":" + aHex(sha256(password, salt));
    }

    public static boolean verificar(String password, String almacenado) {
        if (almacenado == null) {
            return false;
        }
        String[] partes = almacenado.split(":");
        if (partes.length != 2) {
            return false;
        }
        byte[] salt = desdeHex(partes[0]);
        return aHex(sha256(password, salt)).equals(partes[1]);
    }

    private static byte[] sha256(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(password.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("No se pudo hashear la contrasena", e);
        }
    }

    private static String aHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String h = Integer.toHexString(bytes[i] & 0xFF);
            if (h.length() == 1) {
                sb.append('0');
            }
            sb.append(h);
        }
        return sb.toString();
    }

    private static byte[] desdeHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
}