package com.example.todolistapp.data;

import android.content.Context;

import com.example.todolistapp.model.Categoria;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository extends JsonRepository<Categoria> {

    private static CategoriaRepository instancia;

    private CategoriaRepository(Context context) {
        super(context, "categorias");
    }

    public static synchronized CategoriaRepository getInstance(Context context) {
        if (instancia == null) {
            instancia = new CategoriaRepository(context);
        }
        return instancia;
    }

    @Override
    protected Type getTipoLista() {
        return new TypeToken<List<Categoria>>() {
        }.getType();
    }

    public List<Categoria> findByUsuario(int usuarioId) {
        List<Categoria> resultado = new ArrayList<Categoria>();
        for (Categoria c : getCache()) {
            if (c.getUsuarioId() == usuarioId) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    /**
     * Alta con color automatico. Por eso la pantalla de categorias es un solo
     * input de texto: el usuario no elige color y nunca repite dos veces el mismo.
     */
    public Categoria crear(int usuarioId, String nombre) {
        Categoria c = new Categoria(usuarioId, nombre.trim(), proximoColorId(usuarioId));
        return create(c);
    }

    /** Primer indice libre entre 0 y 4. Si estan todos usados, cicla. */
    private int proximoColorId(int usuarioId) {
        List<Categoria> propias = findByUsuario(usuarioId);
        boolean[] usados = new boolean[Categoria.COLORES_ASIGNABLES];
        for (Categoria c : propias) {
            if (c.getColorId() >= 0 && c.getColorId() < Categoria.COLORES_ASIGNABLES) {
                usados[c.getColorId()] = true;
            }
        }
        for (int i = 0; i < usados.length; i++) {
            if (!usados[i]) {
                return i;
            }
        }
        return propias.size() % Categoria.COLORES_ASIGNABLES;
    }

    public boolean existeNombre(int usuarioId, String nombre) {
        String buscado = nombre.trim().toLowerCase();
        for (Categoria c : findByUsuario(usuarioId)) {
            if (c.getNombre().toLowerCase().equals(buscado)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Al borrar una categoria, las tareas que la tenian quedarian apuntando a
     * un id que ya no existe. Se desasignan antes de borrar.
     */
    @Override
    public void delete(int id) {
        TareaRepository.getInstance(appContext).desasignarCategoria(id);
        super.delete(id);
    }
}