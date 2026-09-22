package com.example.todolistapp.model;

public class Categoria implements Identificable {

    /**
     * Cantidad de pares de color asignables (indices 0..4).
     * El indice 5 queda reservado para "Sin categoria" y no se asigna nunca aca.
     */
    public static final int COLORES_ASIGNABLES = 5;

    /** Indice gris, para las tareas que no tienen categoria. */
    public static final int COLOR_SIN_CATEGORIA = 5;

    private int id;
    private int usuarioId;
    private String nombre;

    /** Indice del par de colores en R.array.tag_bg / R.array.tag_text. */
    private int colorId;

    private long createdAt;

    public Categoria() {
    }

    public Categoria(int usuarioId, String nombre, int colorId) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.colorId = colorId;
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

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /** Util si en algun momento tiras la categoria dentro de un Spinner. */
    @Override
    public String toString() {
        return nombre;
    }
}