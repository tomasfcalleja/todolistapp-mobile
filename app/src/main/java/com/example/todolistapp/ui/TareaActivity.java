package com.example.todolistapp.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.data.CategoriaRepository;
import com.example.todolistapp.data.SessionManager;
import com.example.todolistapp.data.TareaRepository;
import com.example.todolistapp.model.Tarea;
import com.google.android.material.button.MaterialButton;

/**
 * Sirve para crear y para editar. La diferencia la marca el extra del Intent:
 * si viene un id, es edicion; si no, es alta. Una sola Activity y un solo
 * layout en vez de dos casi identicos.
 */
public class TareaActivity extends AppCompatActivity implements ChipAdapter.OnFiltroListener {

    private static final String EXTRA_TAREA_ID = "tarea_id";
    private static final int SIN_ID = -1;

    public static Intent nueva(Context context) {
        return new Intent(context, TareaActivity.class);
    }

    public static Intent editar(Context context, int tareaId) {
        Intent i = new Intent(context, TareaActivity.class);
        i.putExtra(EXTRA_TAREA_ID, tareaId);
        return i;
    }

    private TareaRepository tareas;
    private ChipAdapter chipAdapter;

    private EditText etTitulo;
    private EditText etDescripcion;

    private int usuarioId;
    private int tareaId = SIN_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea);

        usuarioId = new SessionManager(this).getUsuarioId();
        tareas = TareaRepository.getInstance(this);
        tareaId = getIntent().getIntExtra(EXTRA_TAREA_ID, SIN_ID);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        TextView tvTituloPantalla = findViewById(R.id.tvTituloPantalla);
        MaterialButton btnGuardar = findViewById(R.id.btnGuardar);
        MaterialButton btnEliminar = findViewById(R.id.btnEliminar);

        chipAdapter = new ChipAdapter(this, this, getString(R.string.sin_categoria));
        RecyclerView rvChips = findViewById(R.id.rvChipsCategoria);
        rvChips.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvChips.setAdapter(chipAdapter);
        chipAdapter.setCategorias(
                CategoriaRepository.getInstance(this).findByUsuario(usuarioId));

        boolean esEdicion = tareaId != SIN_ID;
        if (esEdicion) {
            Tarea t = tareas.findById(tareaId);
            if (t == null) {
                finish();
                return;
            }
            tvTituloPantalla.setText(R.string.tarea_titulo_editar);
            etTitulo.setText(t.getTitulo());
            etDescripcion.setText(t.getDescripcion());
            chipAdapter.setSeleccionadaId(t.getCategoriaId());
            btnEliminar.setVisibility(View.VISIBLE);
        } else {
            tvTituloPantalla.setText(R.string.tarea_titulo_nueva);
            btnEliminar.setVisibility(View.GONE);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarEliminar();
            }
        });

        findViewById(R.id.tvCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void guardar() {
        String titulo = etTitulo.getText().toString().trim();
        if (TextUtils.isEmpty(titulo)) {
            Toast.makeText(this, R.string.error_titulo_vacio, Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcion = etDescripcion.getText().toString().trim();
        Integer categoriaId = chipAdapter.getSeleccionadaId();

        if (tareaId == SIN_ID) {
            Tarea t = new Tarea(usuarioId, categoriaId, titulo);
            t.setDescripcion(descripcion);
            tareas.create(t);
        } else {
            Tarea t = tareas.findById(tareaId);
            t.setTitulo(titulo);
            t.setDescripcion(descripcion);
            t.setCategoriaId(categoriaId);
            tareas.update(t);
        }

        finish();
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tarea_eliminar_titulo)
                .setMessage(R.string.tarea_eliminar_mensaje)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tareas.delete(tareaId);
                        finish();
                    }
                })
                .show();
    }

    /** El chip ya guarda la seleccion adentro del adapter; aca no hay nada que hacer. */
    @Override
    public void onFiltroSeleccionado(Integer categoriaId) {
    }
}