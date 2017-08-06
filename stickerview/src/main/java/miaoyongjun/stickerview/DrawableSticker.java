package miaoyongjun.stickerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Author: miaoyongjun
 * Date : 17/8/6
 */

public class DrawableSticker extends Sticker {
    private Drawable drawable;
    private Rect realBounds;

    public DrawableSticker(Drawable drawable) {
        this.drawable = drawable;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
        init(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        drawable.setBounds(realBounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getWidth() {
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getIntrinsicHeight();
    }
}
