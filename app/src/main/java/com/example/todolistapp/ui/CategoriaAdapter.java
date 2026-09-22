package com.example.todolistapp.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    public interface OnCategoriaListener {
        void onEliminar(Categoria categoria);
    }

    private final Context context;
    private final LayoutInflater inflater;
    private final OnCategoriaListener listener;
    private final int[] coloresFondo;
    private final int[] coloresTexto;

    private List<Categoria> categorias = new ArrayList<Categoria>();

    /** Pendientes por categoria, en el mismo orden que la lista. */
    private int[] pendientes = new int[0];

    public CategoriaAdapter(Context context, OnCategoriaListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.coloresFondo = leerArray(context, R.array.tag_bg);
        this.coloresTexto = leerArray(context, R.array.tag_text);
    }

    private static int[] leerArray(Context context, int arrayRes) {
        TypedArray ta = context.getResources().obtainTypedArray(arrayRes);
        int[] colores = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colores[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        return colores;
    }

    public void setDatos(List<Categoria> categorias, int[] pendientes) {
        this.categorias = categorias;
        this.pendientes = pendientes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        final Categoria c = categorias.get(position);

        int indice = c.getColorId();
        if (indice < 0 || indice >= coloresFondo.length) {
            indice = Categoria.COLOR_SIN_CATEGORIA;
        }

        holder.tvNombre.setText(c.getNombre());
        holder.tvNombre.setTextColor(coloresTexto[indice]);
        ViewCompat.setBackgroundTintList(holder.tvNombre,
                ColorStateList.valueOf(coloresFondo[indice]));

        int cantidad = (position < pendientes.length) ? pendientes[position] : 0;
        holder.tvPendientes.setText(context.getResources()
                .getQuantityString(R.plurals.categoria_pendientes, cantidad, cantidad));

        holder.ivEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEliminar(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        final TextView tvNombre;
        final TextView tvPendientes;
        final ImageView ivEliminar;

        CategoriaViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPendientes = itemView.findViewById(R.id.tvPendientes);
            ivEliminar = itemView.findViewById(R.id.ivEliminar);
        }
    }
}