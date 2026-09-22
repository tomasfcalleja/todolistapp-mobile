package com.example.todolistapp.data;

import android.content.Context;

import com.example.todolistapp.model.Tarea;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TareaRepository extends JsonRepository<Tarea> {

    private static TareaRepository instancia;

    private TareaRepository(Context context) {
        super(context, "tareas");
    }

    public static synchronized TareaRepository getInstance(Context context) {
        if (instancia == null) {
            instancia = new TareaRepository(context);
        }
        return instancia;
    }

    @Override
    protected Type getTipoLista() {
        return new TypeToken<List<Tarea>>() {
        }.getType();
    }

    /** Pendientes primero, y dentro de cada grupo las mas nuevas arriba. */
    public List<Tarea> findByUsuario(int usuarioId) {
        List<Tarea> resultado = new ArrayList<Tarea>();
        for (Tarea t : getCache()) {
            if (t.getUsuarioId() == usuarioId) {
                resultado.add(t);
            }
        }
        ordenar(resultado);
        return resultado;
    }

    /** El filtro del home. categoriaId null trae las tareas sin categoria. */
    public List<Tarea> findByCategoria(int usuarioId, Integer categoriaId) {
        List<Tarea> resultado = new ArrayList<Tarea>();
        for (Tarea t : getCache()) {
            if (t.getUsuarioId() != usuarioId) {
                continue;
            }
            if (categoriaId == null) {
                if (t.getCategoriaId() == null) {
                    resultado.add(t);
                }
            } else {
                if (t.getCategoriaId() != null
                        && t.getCategoriaId().intValue() == categoriaId.intValue()) {
                    resultado.add(t);
                }
            }
        }
        ordenar(resultado);
        return resultado;
    }

    /** Para el contador de la pantalla de categorias. */
    public int contarPendientes(int usuarioId, int categoriaId) {
        int total = 0;
        for (Tarea t : getCache()) {
            if (t.getUsuarioId() == usuarioId
                    && !t.isCompletada()
                    && t.getCategoriaId() != null
                    && t.getCategoriaId().intValue() == categoriaId) {
                total++;
            }
        }
        return total;
    }

    /** Para el checkbox de cada fila del RecyclerView. */
    public void toggleCompletada(int tareaId) {
        Tarea t = findById(tareaId);
        if (t != null) {
            t.setCompletada(!t.isCompletada());
            persist();
        }
    }

    /** Llamado por CategoriaRepository.delete(). Package-private a proposito. */
    void desasignarCategoria(int categoriaId) {
        boolean huboCambios = false;
        for (Tarea t : getCache()) {
            if (t.getCategoriaId() != null
                    && t.getCategoriaId().intValue() == categoriaId) {
                t.setCategoriaId(null);
                huboCambios = true;
            }
        }
        if (huboCambios) {
            persist();
        }
    }

    private void ordenar(List<Tarea> lista) {
        Collections.sort(lista, new Comparator<Tarea>() {
            @Override
            public int compare(Tarea a, Tarea b) {
                if (a.isCompletada() != b.isCompletada()) {
                    return a.isCompletada() ? 1 : -1;
                }
                if (b.getCreatedAt() > a.getCreatedAt()) {
                    return 1;
                }
                if (b.getCreatedAt() < a.getCreatedAt()) {
                    return -1;
                }
                return 0;
            }
        });
    }
}