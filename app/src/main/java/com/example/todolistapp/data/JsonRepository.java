package com.example.todolistapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.todolistapp.model.Identificable;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio generico sobre SharedPreferences + Gson.
 *
 * Cada subclase guarda su lista completa serializada como JSON bajo su
 * propia clave. Mantiene una cache en memoria para no parsear el JSON en
 * cada lectura: por eso los repositorios son singleton, si hubiera dos
 * instancias del mismo repo cada una tendria su cache y se pisarian.
 */
public abstract class JsonRepository<T extends Identificable> {

    public static final String PREFS_NAME = "todolist_data";

    protected static final Gson GSON = new Gson();

    protected final Context appContext;
    protected final SharedPreferences prefs;

    private final String key;
    private List<T> cache;

    protected JsonRepository(Context context, String key) {
        this.appContext = context.getApplicationContext();
        this.prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.key = key;
    }

    /**
     * Java borra los genericos en tiempo de ejecucion, asi que Gson no puede
     * saber solo que hay adentro de la lista. Cada subclase se lo dice con
     * un TypeToken.
     */
    protected abstract Type getTipoLista();

    protected List<T> getCache() {
        if (cache == null) {
            cargar();
        }
        return cache;
    }

    private void cargar() {
        String json = prefs.getString(key, null);
        if (json == null) {
            cache = new ArrayList<T>();
            return;
        }
        List<T> lista = GSON.fromJson(json, getTipoLista());
        cache = (lista != null) ? lista : new ArrayList<T>();
    }

    protected void persist() {
        prefs.edit().putString(key, GSON.toJson(getCache())).apply();
    }

    private int proximoId() {
        int max = 0;
        for (T item : getCache()) {
            if (item.getId() > max) {
                max = item.getId();
            }
        }
        return max + 1;
    }

    public T create(T item) {
        item.setId(proximoId());
        getCache().add(item);
        persist();
        return item;
    }

    public void update(T item) {
        List<T> lista = getCache();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == item.getId()) {
                lista.set(i, item);
                persist();
                return;
            }
        }
    }

    public void delete(int id) {
        List<T> lista = getCache();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                lista.remove(i);
                persist();
                return;
            }
        }
    }

    public T findById(int id) {
        for (T item : getCache()) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    /** Copia defensiva: si devolvieras la cache, cualquiera podria modificarla. */
    public List<T> findAll() {
        return new ArrayList<T>(getCache());
    }

    /** Solo para desarrollo: borra todo lo guardado bajo esta clave. */
    public void borrarTodo() {
        getCache().clear();
        persist();
    }
}