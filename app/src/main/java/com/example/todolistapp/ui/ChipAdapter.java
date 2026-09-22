package com.example.todolistapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.model.Categoria;

import java.util.ArrayList;
import java.util.List;

/**
 * Fila de chips de categoria. Se usa en dos lugares:
 * en el home como filtro (el primer chip dice "Todas") y en la pantalla de
 * tarea como selector (el primer chip dice "Sin categoria"). Solo cambia la
 * etiqueta del primero; el comportamiento es el mismo y en ambos casos
 * significa "ninguna categoria en particular".
 */
public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ChipViewHolder> {

    public interface OnFiltroListener {
        /** null = el primer chip. */
        void onFiltroSeleccionado(Integer categoriaId);
    }

    private final LayoutInflater inflater;
    private final OnFiltroListener listener;
    private final String etiquetaPrimero;

    private List<Categoria> categorias = new ArrayList<Categoria>();

    /**
     * Se guarda el id y no la posicion: si borras una categoria, las
     * posiciones se corren y el chip marcado seria otro.
     */
    private Integer seleccionadaId = null;

    /** Version del home: el primer chip dice "Todas". */
    public ChipAdapter(Context context, OnFiltroListener listener) {
        this(context, listener, context.getString(R.string.chip_todas));
    }

    public ChipAdapter(Context context, OnFiltroListener listener, String etiquetaPrimero) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.etiquetaPrimero = etiquetaPrimero;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
        // Si la categoria seleccionada ya no existe, vuelve al primer chip.
        if (seleccionadaId != null && buscarPosicion(seleccionadaId) == -1) {
            seleccionadaId = null;
        }
        notifyDataSetChanged();
    }

    private int buscarPosicion(Integer id) {
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == id.intValue()) {
                return i;
            }
        }
        return -1;
    }

    public Integer getSeleccionadaId() {
        return seleccionadaId;
    }

    /** Para precargar el chip al editar una tarea. No dispara el listener. */
    public void setSeleccionadaId(Integer id) {
        this.seleccionadaId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_chip, parent, false);
        return new ChipViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        final Integer id;
        String texto;

        if (position == 0) {
            id = null;
            texto = etiquetaPrimero;
        } else {
            Categoria c = categorias.get(position - 1);
            id = Integer.valueOf(c.getId());
            texto = c.getNombre();
        }

        holder.tvChip.setText(texto);

        boolean activo = (id == null && seleccionadaId == null)
                || (id != null && seleccionadaId != null
                && id.intValue() == seleccionadaId.intValue());
        holder.tvChip.setSelected(activo);

        holder.tvChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionadaId = id;
                notifyDataSetChanged();
                listener.onFiltroSeleccionado(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size() + 1;
    }

    static class ChipViewHolder extends RecyclerView.ViewHolder {

        final TextView tvChip;

        ChipViewHolder(View itemView) {
            super(itemView);
            tvChip = itemView.findViewById(R.id.tvChip);
        }
    }
}