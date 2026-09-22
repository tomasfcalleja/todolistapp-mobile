package com.example.todolistapp.data;

import android.content.Context;

import com.example.todolistapp.model.Usuario;
import com.example.todolistapp.util.HashUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class UsuarioRepository extends JsonRepository<Usuario> {

    private static UsuarioRepository instancia;

    private UsuarioRepository(Context context) {
        super(context, "usuarios");
    }

    public static synchronized UsuarioRepository getInstance(Context context) {
        if (instancia == null) {
            instancia = new UsuarioRepository(context);
        }
        return instancia;
    }

    @Override
    protected Type getTipoLista() {
        return new TypeToken<List<Usuario>>() {
        }.getType();
    }

    public Usuario findByEmail(String email) {
        if (email == null) {
            return null;
        }
        String buscado = email.trim().toLowerCase();
        for (Usuario u : getCache()) {
            if (u.getEmail() != null && u.getEmail().equals(buscado)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Devuelve el usuario creado, o null si el email ya estaba registrado.
     * El hasheo pasa aca adentro: la Activity nunca ve un hash.
     */
    public Usuario registrar(String email, String password, String nombre) {
        String normalizado = email.trim().toLowerCase();
        if (findByEmail(normalizado) != null) {
            return null;
        }
        Usuario u = new Usuario(normalizado, HashUtil.hash(password), nombre);
        return create(u);
    }

    /** Devuelve el usuario si las credenciales son validas, null si no. */
    public Usuario autenticar(String email, String password) {
        Usuario u = findByEmail(email);
        if (u == null) {
            return null;
        }
        if (HashUtil.verificar(password, u.getPasswordHash())) {
            return u;
        }
        return null;
    }
}