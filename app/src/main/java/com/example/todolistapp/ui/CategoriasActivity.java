package com.example.todolistapp.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.data.CategoriaRepository;
import com.example.todolistapp.data.SessionManager;
import com.example.todolistapp.data.TareaRepository;
import com.example.todolistapp.model.Categoria;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CategoriasActivity extends AppCompatActivity
        implements CategoriaAdapter.OnCategoriaListener {

    public static Intent abrir(Context context) {
        return new Intent(context, CategoriasActivity.class);
    }

    private CategoriaRepository categorias;
    private TareaRepository tareas;
    private CategoriaAdapter adapter;

    private EditText etNueva;
    private View tvVacio;
    private RecyclerView rvCategorias;

    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        usuarioId = new SessionManager(this).getUsuarioId();
        categorias = CategoriaRepository.getInstance(this);
        tareas = TareaRepository.getInstance(this);

        etNueva = findViewById(R.id.etNueva);
        tvVacio = findViewById(R.id.tvVacio);
        rvCategorias = findViewById(R.id.rvCategorias);

        adapter = new CategoriaAdapter(this, this);
        rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        rvCategorias.setAdapter(adapter);
        rvCategorias.addItemDecoration(new EspacioVertical(
                getResources().getDimensionPixelSize(R.dimen.gap_tarjetas)));

        MaterialButton btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar();
            }
        });

        findViewById(R.id.tvVolver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recargar();
    }

    private void recargar() {
        List<Categoria> lista = categorias.findByUsuario(usuarioId);

        int[] pendientes = new int[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            pendientes[i] = tareas.contarPendientes(usuarioId, lista.get(i).getId());
        }

        adapter.setDatos(lista, pendientes);

        boolean vacio = lista.isEmpty();
        tvVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        rvCategorias.setVisibility(vacio ? View.GONE : View.VISIBLE);
    }

    private void agregar() {
        String nombre = etNueva.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, R.string.error_nombre_vacio, Toast.LENGTH_SHORT).show();
            return;
        }
        if (categorias.existeNombre(usuarioId, nombre)) {
            Toast.makeText(this, R.string.error_categoria_repetida, Toast.LENGTH_SHORT).show();
            return;
        }

        categorias.crear(usuarioId, nombre);
        etNueva.setText("");
        recargar();
    }

    @Override
    public void onEliminar(final Categoria categoria) {
        int pendientes = tareas.contarPendientes(usuarioId, categoria.getId());
        String mensaje = (pendientes > 0)
                ? getString(R.string.categoria_eliminar_con_tareas, pendientes)
                : getString(R.string.categoria_eliminar_mensaje);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.categoria_eliminar_titulo, categoria.getNombre()))
                .setMessage(mensaje)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categorias.delete(categoria.getId());
                        recargar();
                    }
                })
                .show();
    }
}