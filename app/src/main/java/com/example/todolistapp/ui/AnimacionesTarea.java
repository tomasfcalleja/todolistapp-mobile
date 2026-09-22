package com.example.todolistapp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.example.todolistapp.R;

public final class AnimacionesTarea {

    public static final float ALPHA_COMPLETADA = 0.45f;

    private AnimacionesTarea() {}

    public static void completar(View tarjeta, View check, View destello,
                                 TachadoTextView titulo, int colorFinal,
                                 Runnable cambiarIcono, Runnable alTerminar) {
        int haptica = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                ? HapticFeedbackConstants.CONFIRM
                : HapticFeedbackConstants.KEYBOARD_TAP;
        check.performHapticFeedback(haptica);

        // 1a. Pop del check
        check.animate().scaleX(0.7f).scaleY(0.7f).setDuration(90)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    cambiarIcono.run();
                    check.animate().scaleX(1f).scaleY(1f).setDuration(400)
                            .setInterpolator(new OvershootInterpolator(3f)).start();
                }).start();

        // 1b. Destello
        destello.setScaleX(1f);
        destello.setScaleY(1f);
        destello.setAlpha(0.3f);
        destello.animate().scaleX(2.2f).scaleY(2.2f).alpha(0f)
                .setStartDelay(60).setDuration(500)
                .setInterpolator(new DecelerateInterpolator()).start();

        // 2. Tachado de izquierda a derecha
        ValueAnimator tachado = ValueAnimator.ofFloat(0f, 1f);
        tachado.setStartDelay(150);
        tachado.setDuration(450);
        tachado.setInterpolator(new AccelerateDecelerateInterpolator());
        tachado.addUpdateListener(a -> titulo.setProgreso((float) a.getAnimatedValue()));
        tachado.addListener(new AnimatorListenerAdapter() {
            private boolean cancelada = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                cancelada = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (cancelada) return;
                // 3. "Sello" + opacidad y color de completada
                animarColor(titulo, colorFinal, 250);
                tarjeta.animate().scaleX(0.97f).scaleY(0.97f)
                        .alpha(ALPHA_COMPLETADA).setDuration(110)
                        .withEndAction(() -> tarjeta.animate()
                                .scaleX(1f).scaleY(1f).setDuration(320)
                                .setInterpolator(new OvershootInterpolator(2.5f))
                                .withEndAction(alTerminar)
                                .start())
                        .start();
            }
        });
        titulo.setTag(R.id.tvTitulo, tachado);
        tachado.start();
    }

    public static void descompletar(View tarjeta, TachadoTextView titulo, int colorFinal,
                                    Runnable cambiarIcono, Runnable alTerminar) {
        cambiarIcono.run();
        tarjeta.animate().alpha(1f).setDuration(200).start();
        animarColor(titulo, colorFinal, 200);

        ValueAnimator destachado = ValueAnimator.ofFloat(titulo.getProgreso(), 0f);
        destachado.setDuration(200);
        destachado.addUpdateListener(a -> titulo.setProgreso((float) a.getAnimatedValue()));
        destachado.addListener(new AnimatorListenerAdapter() {
            private boolean cancelada = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                cancelada = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!cancelada) alTerminar.run();
            }
        });
        titulo.setTag(R.id.tvTitulo, destachado);
        destachado.start();
    }

    /** Frena cualquier animación en curso de una fila (se llama al reciclarla). */
    public static void cancelar(View tarjeta, View check, View destello, TachadoTextView titulo) {
        Object anim = titulo.getTag(R.id.tvTitulo);
        if (anim instanceof ValueAnimator) {
            ((ValueAnimator) anim).cancel();
        }
        titulo.setTag(R.id.tvTitulo, null);
        tarjeta.animate().cancel();
        check.animate().cancel();
        destello.animate().cancel();
    }

    private static void animarColor(TextView tv, int hasta, long duracion) {
        ValueAnimator a = ValueAnimator.ofArgb(tv.getCurrentTextColor(), hasta);
        a.setDuration(duracion);
        a.addUpdateListener(an -> tv.setTextColor((int) an.getAnimatedValue()));
        a.start();
    }
}