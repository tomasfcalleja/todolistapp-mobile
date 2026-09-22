package com.example.todolistapp.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.todolistapp.R;
import com.example.todolistapp.model.Categoria;
import com.example.todolistapp.model.Tarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    /** La Activity decide que hacer: el adapter solo avisa. */
    public interface OnTareaListener {
        void onToggle(Tarea tarea);

        void onClick(Tarea tarea);
    }

    private final Context context;
    private final LayoutInflater inflater;
    private final OnTareaListener listener;

    /** Los arrays de color se resuelven una sola vez, no en cada bind. */
    private final int[] coloresFondo;
    private final int[] coloresTexto;
    private final int colorInk;
    private final int colorMuted;

    private List<Tarea> tareas = new ArrayList<Tarea>();
    private List<Categoria> categorias = new ArrayList<Categoria>();

    /** NUEVO: "foto" del contenido de cada tarea en el último setDatos, para el diff. */
    private final Map<Integer, String> firmas = new HashMap<Integer, String>();

    private RecyclerView recyclerView;

    public TareaAdapter(Context context, OnTareaListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.coloresFondo = leerArray(context, R.array.tag_bg);
        this.coloresTexto = leerArray(context, R.array.tag_text);
        this.colorInk = ContextCompat.getColor(context, R.color.ink);
        this.colorMuted = ContextCompat.getColor(context, R.color.muted);
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

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView rv) {
        super.onAttachedToRecyclerView(rv);
        recyclerView = rv;
        // NUEVO: sin esto, cada cambio hace un crossfade que pisa nuestra animación
        if (rv.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView rv) {
        super.onDetachedFromRecyclerView(rv);
        recyclerView = null;
    }

    /** NUEVO: con DiffUtil, los cambios se animan (la tarea completada viaja al fondo). */
    public void setDatos(List<Tarea> nuevas, List<Categoria> nuevasCategorias) {
        final List<Tarea> viejas = this.tareas;
        final Map<Integer, String> firmasViejas = new HashMap<Integer, String>(firmas);

        this.categorias = nuevasCategorias;
        final Map<Integer, String> firmasNuevas = new HashMap<Integer, String>();
        for (Tarea t : nuevas) {
            firmasNuevas.put(t.getId(), firma(t));
        }

        // Copia propia: si la Activity reutiliza su lista, el diff igual funciona
        this.tareas = new ArrayList<Tarea>(nuevas);
        firmas.clear();
        firmas.putAll(firmasNuevas);

        // Primera carga: redibujo completo, así corre la animación de entrada del home
        if (viejas.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return viejas.size();
            }

            @Override
            public int getNewListSize() {
                return tareas.size();
            }

            @Override
            public boolean areItemsTheSame(int o, int n) {
                return viejas.get(o).getId() == tareas.get(n).getId();
            }

            @Override
            public boolean areContentsTheSame(int o, int n) {
                String antes = firmasViejas.get(viejas.get(o).getId());
                String ahora = firmasNuevas.get(tareas.get(n).getId());
                return antes != null && antes.equals(ahora);
            }
        });

        // Si la tarea que se mueve es la primera visible, el RecyclerView tiende a
        // "seguirla" con el scroll. Guardar y restaurar la posición lo evita.
        RecyclerView.LayoutManager lm = recyclerView != null ? recyclerView.getLayoutManager() : null;
        Parcelable estado = lm != null ? lm.onSaveInstanceState() : null;
        diff.dispatchUpdatesTo(this);
        if (lm != null && estado != null) {
            lm.onRestoreInstanceState(estado);
        }
    }

    private String firma(Tarea t) {
        Categoria c = buscarCategoria(t.getCategoriaId());
        return t.getTitulo() + "|" + t.isCompletada() + "|"
                + (c != null ? c.getNombre() + "|" + c.getColorId() : "-");
    }

    private Categoria buscarCategoria(Integer categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        for (Categoria c : categorias) {
            if (c.getId() == categoriaId.intValue()) {
                return c;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TareaViewHolder holder, int position) {
        final Tarea tarea = tareas.get(position);

        // NUEVO: si la fila venía de otra tarea con una animación a medias, se frena
        AnimacionesTarea.cancelar(holder.itemView, holder.contenedorCheck,
                holder.vDestello, holder.tvTitulo);
        holder.itemView.setScaleX(1f);
        holder.itemView.setScaleY(1f);
        holder.contenedorCheck.setScaleX(1f);
        holder.contenedorCheck.setScaleY(1f);
        holder.vDestello.setAlpha(0f);

        Categoria categoria = buscarCategoria(tarea.getCategoriaId());
        String nombre;
        int indiceColor;
        if (categoria != null) {
            nombre = categoria.getNombre();
            indiceColor = categoria.getColorId();
        } else {
            nombre = context.getString(R.string.sin_categoria);
            indiceColor = Categoria.COLOR_SIN_CATEGORIA;
        }
        if (indiceColor < 0 || indiceColor >= coloresFondo.length) {
            indiceColor = Categoria.COLOR_SIN_CATEGORIA;
        }

        holder.tvCategoria.setText(nombre);
        holder.tvCategoria.setTextColor(coloresTexto[indiceColor]);
        ViewCompat.setBackgroundTintList(holder.tvCategoria,
                ColorStateList.valueOf(coloresFondo[indiceColor]));

        holder.tvTitulo.setText(tarea.getTitulo());

        // Las dos ramas siempre completas: las views se reciclan y el estado
        // anterior se arrastra si no lo reseteas.
        boolean completada = tarea.isCompletada();
        holder.itemView.setAlpha(completada ? AnimacionesTarea.ALPHA_COMPLETADA : 1f);
        holder.tvTitulo.setTextColor(completada ? colorMuted : colorInk);
        holder.tvTitulo.setProgreso(completada ? 1f : 0f); // NUEVO: reemplaza el STRIKE_THRU
        holder.ivCheck.setVisibility(completada ? View.VISIBLE : View.GONE);
        holder.vCheckbox.setSelected(completada);

        // NUEVO: primero se anima, y recién al terminar se avisa a la Activity
        holder.contenedorCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false); // evita doble toque durante la animación
                final boolean completar = !tarea.isCompletada();

                Runnable cambiarIcono = new Runnable() {
                    @Override
                    public void run() {
                        holder.ivCheck.setVisibility(completar ? View.VISIBLE : View.GONE);
                        holder.vCheckbox.setSelected(completar);
                    }
                };
                Runnable avisar = new Runnable() {
                    @Override
                    public void run() {
                        listener.onToggle(tarea);
                    }
                };

                if (completar) {
                    AnimacionesTarea.completar(holder.itemView, holder.contenedorCheck,
                            holder.vDestello, holder.tvTitulo, colorMuted,
                            cambiarIcono, avisar);
                } else {
                    AnimacionesTarea.descompletar(holder.itemView, holder.tvTitulo,
                            colorInk, cambiarIcono, avisar);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(tarea);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    static class TareaViewHolder extends RecyclerView.ViewHolder {

        final View contenedorCheck;
        final View vCheckbox;
        final View vDestello;
        final ImageView ivCheck;
        final TextView tvCategoria;
        final TachadoTextView tvTitulo;

        TareaViewHolder(View itemView) {
            super(itemView);
            contenedorCheck = itemView.findViewById(R.id.contenedorCheck);
            vCheckbox = itemView.findViewById(R.id.vCheckbox);
            vDestello = itemView.findViewById(R.id.vDestello);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
        }
    }
}