package com.example.todolistapp.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Separacion entre items de una lista vertical. Va como ItemDecoration y no
 * como margin en el item: asi el ultimo no arrastra un espacio de mas abajo.
 */
public class EspacioVertical extends RecyclerView.ItemDecoration {

    private final int espacio;

    public EspacioVertical(int espacio) {
        this.espacio = espacio;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) > 0) {
            outRect.top = espacio;
        }
    }
}
