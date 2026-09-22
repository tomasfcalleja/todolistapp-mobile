package com.example.todolistapp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class TachadoTextView extends AppCompatTextView {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progreso = 0f;

    public TachadoTextView(Context context) { super(context); init(); }
    public TachadoTextView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public TachadoTextView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); init(); }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1.5f * getResources().getDisplayMetrics().density);
    }

    public void setProgreso(float p) {
        progreso = p;
        invalidate();
    }

    public float getProgreso() {
        return progreso;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Layout layout = getLayout();
        if (progreso <= 0f || layout == null) return;

        paint.setColor(getCurrentTextColor());

        // Largo total del texto sumando todas las líneas
        float total = 0f;
        for (int i = 0; i < layout.getLineCount(); i++) {
            total += layout.getLineRight(i) - layout.getLineLeft(i);
        }

        float restante = total * progreso;
        float offX = getTotalPaddingLeft();
        float offY = getTotalPaddingTop();

        for (int i = 0; i < layout.getLineCount() && restante > 0; i++) {
            float izq = layout.getLineLeft(i);
            float anchoLinea = layout.getLineRight(i) - izq;
            float largo = Math.min(anchoLinea, restante);
            float y = layout.getLineBaseline(i) - getTextSize() * 0.3f; // centro de la minúscula
            canvas.drawLine(offX + izq, offY + y, offX + izq + largo, offY + y, paint);
            restante -= anchoLinea;
        }
    }
}