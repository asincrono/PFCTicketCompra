package es.dexusta.ticketcompra.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by asincrono on 17/04/14.
 */
public class SlideLinearLayout extends LinearLayout{

    public SlideLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction() {
        return getX()/getWidth();
    }

    public void setXFraction(float xFraction) {
        setX(xFraction * getWidth());
    }
}
