package com.example.todolistapp.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Guarda quien esta logueado. Con esto, al abrir la app podes saltear
 * el login si ya hay sesion abierta.
 */
public class SessionManager {

    private static final String KEY_USUARIO_ID = "session_usuario_id";
    private static final int SIN_SESION = -1;

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(JsonRepository.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void iniciarSesion(int usuarioId) {
        prefs.edit().putInt(KEY_USUARIO_ID, usuarioId).apply();
    }

    public void cerrarSesion() {
        prefs.edit().remove(KEY_USUARIO_ID).apply();
    }

    public boolean haySesion() {
        return getUsuarioId() != SIN_SESION;
    }

    /** Devuelve -1 si no hay nadie logueado. */
    public int getUsuarioId() {
        return prefs.getInt(KEY_USUARIO_ID, SIN_SESION);
    }
}