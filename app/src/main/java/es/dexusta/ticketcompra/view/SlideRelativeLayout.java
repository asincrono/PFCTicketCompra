package es.dexusta.ticketcompra.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by asincrono on 13/04/14.
 */
public class SlideRelativeLayout extends RelativeLayout {
    public SlideRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction() {
        return getX() / getWidth();
    }

    public void setXFraction(float xFraction) {
        setX(xFraction * getWidth());
    }
}
