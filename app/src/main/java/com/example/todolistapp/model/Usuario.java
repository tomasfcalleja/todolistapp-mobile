package com.example.todolistapp.model;

import com.example.todolistapp.model.Identificable;

public class Usuario implements Identificable {

    private int id;
    private String email;

    /** Nunca la contrasena en texto plano. Formato "salt:hash", ver HashUtil. */
    private String passwordHash;

    private String nombre;
    private long createdAt;

    /** Gson necesita el constructor vacio para deserializar. */
    public Usuario() {
    }

    public Usuario(String email, String passwordHash, String nombre) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nombre = nombre;
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}