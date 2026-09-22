package com.example.todolistapp.model;

public class Tarea implements Identificable {

    public static final int PRIORIDAD_BAJA = 0;
    public static final int PRIORIDAD_MEDIA = 1;
    public static final int PRIORIDAD_ALTA = 2;

    private int id;
    private int usuarioId;

    /** null = tarea sin categoria. Por eso es Integer y no int. */
    private Integer categoriaId;

    private String titulo;
    private String descripcion;
    private boolean completada;

    /** null = sin fecha de vencimiento. Podes ignorarlo en la UI del MVP. */
    private Long fechaVenc;

    /** Previsto para una extension. No aparece en las pantallas del MVP. */
    private int prioridad;

    private long createdAt;

    /** null mientras la tarea siga pendiente. */
    private Long completedAt;

    public Tarea() {
    }

    public Tarea(int usuarioId, Integer categoriaId, String titulo) {
        this.usuarioId = usuarioId;
        this.categoriaId = categoriaId;
        this.titulo = titulo;
        this.completada = false;
        this.prioridad = PRIORIDAD_MEDIA;
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

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
        this.completedAt = completada ? System.currentTimeMillis() : null;
    }

    public Long getFechaVenc() {
        return fechaVenc;
    }

    public void setFechaVenc(Long fechaVenc) {
        this.fechaVenc = fechaVenc;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Long completedAt) {
        this.completedAt = completedAt;
    }
}