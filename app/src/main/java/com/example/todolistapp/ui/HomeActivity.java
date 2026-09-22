package com.example.todolistapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.data.CategoriaRepository;
import com.example.todolistapp.data.SessionManager;
import com.example.todolistapp.data.TareaRepository;
import com.example.todolistapp.data.UsuarioRepository;
import com.example.todolistapp.model.Categoria;
import com.example.todolistapp.model.Tarea;
import com.example.todolistapp.model.Usuario;

import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements TareaAdapter.OnTareaListener, ChipAdapter.OnFiltroListener {

    private SessionManager sesion;
    private TareaRepository tareas;
    private CategoriaRepository categorias;

    private TareaAdapter tareaAdapter;
    private ChipAdapter chipAdapter;

    private RecyclerView rvTareas;
    private View contenedorVacio;

    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sesion = new SessionManager(this);
        if (!sesion.haySesion()) {
            irAlLogin();
            return;
        }

        setContentView(R.layout.activity_home);

        usuarioId = sesion.getUsuarioId();
        tareas = TareaRepository.getInstance(this);
        categorias = CategoriaRepository.getInstance(this);

        rvTareas = findViewById(R.id.rvTareas);
        contenedorVacio = findViewById(R.id.contenedorVacio);
        TextView tvSaludo = findViewById(R.id.tvSaludo);

        Usuario u = UsuarioRepository.getInstance(this).findById(usuarioId);
        tvSaludo.setText(getString(R.string.home_saludo, u != null ? u.getNombre() : ""));

        tareaAdapter = new TareaAdapter(this, this);
        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        rvTareas.setAdapter(tareaAdapter);

        chipAdapter = new ChipAdapter(this, this);
        RecyclerView rvChips = findViewById(R.id.rvChips);
        rvChips.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvChips.setAdapter(chipAdapter);

        findViewById(R.id.fabNueva).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TareaActivity.nueva(HomeActivity.this));
            }
        });

        findViewById(R.id.tvGestionar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CategoriasActivity.abrir(HomeActivity.this));
            }
        });

        // Provisorio para poder probar el login sin desinstalar la app.
        tvSaludo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sesion.cerrarSesion();
                irAlLogin();
                return true;
            }
        });
    }

    /**
     * En onResume y no en onCreate: al volver de crear o editar una tarea, la
     * Activity no se recrea pero la lista cambio.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (sesion.haySesion()) {
            recargar();
        }
    }

    private void recargar() {
        List<Categoria> cats = categorias.findByUsuario(usuarioId);
        chipAdapter.setCategorias(cats);

        Integer filtro = chipAdapter.getSeleccionadaId();
        List<Tarea> lista;
        if (filtro == null) {
            lista = tareas.findByUsuario(usuarioId);
        } else {
            lista = tareas.findByCategoria(usuarioId, filtro);
        }

        tareaAdapter.setDatos(lista, cats);

        boolean vacio = lista.isEmpty();
        contenedorVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        rvTareas.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onFiltroSeleccionado(Integer categoriaId) {
        recargar();
    }

    @Override
    public void onToggle(Tarea tarea) {
        tareas.toggleCompletada(tarea.getId());
        recargar();
    }

    @Override
    public void onClick(Tarea tarea) {
        startActivity(TareaActivity.editar(this, tarea.getId()));
    }

    private void irAlLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}