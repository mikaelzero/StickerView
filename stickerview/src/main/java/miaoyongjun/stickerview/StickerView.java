package miaoyongjun.stickerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
/**
 * Author: miaoyongjun
 * Date : 17/8/1
 */

public class StickerView extends AppCompatImageView {

    private ArrayList<Sticker> mStickers;

    private Paint mStickerPaint;

    private Bitmap btnDeleteBitmap;

    private Bitmap btnRotateBitmap;

    private Sticker currentSticker;

    private int maxStickerCount;
    private float minStickerSizeScale;

    private PointF lastPoint;

    private TouchState state;

    private float imageBeginScale;
    private int closeIcon, rotateIcon;
    private int closeSize, rotateSize;
    private int outLineWidth, outLineColor;

    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context, attrs);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(context, attrs);
        init(context);
    }

    private void setAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StickerView);
        try {
            imageBeginScale = typedArray.getFloat(R.styleable.StickerView_m_image_init_scale, 0.5f);
            maxStickerCount = typedArray.getInt(R.styleable.StickerView_m_max_count, 20);
            minStickerSizeScale = typedArray.getFloat(R.styleable.StickerView_m_image_min_size_scale, 0.5f);
            closeIcon = typedArray.getResourceId(R.styleable.StickerView_m_close_icon, R.drawable.sticker_closed);
            rotateIcon = typedArray.getResourceId(R.styleable.StickerView_m_rotate_icon, R.drawable.sticker_rotate);
            closeSize = typedArray.getDimensionPixelSize(R.styleable.StickerView_m_close_icon_size, dip2px(context, 15));
            rotateSize = typedArray.getDimensionPixelSize(R.styleable.StickerView_m_rotate_icon_size, dip2px(context, 15));
            outLineWidth = typedArray.getDimensionPixelSize(R.styleable.StickerView_m_outline_width, dip2px(context, 1));
            outLineColor = typedArray.getColor(R.styleable.StickerView_m_outline_color, Color.WHITE);

        } finally {
            typedArray.recycle();
        }

    }

    private void init(Context context) {
        mStickerPaint = new Paint();
        mStickerPaint.setAntiAlias(true);
        mStickerPaint.setStyle(Paint.Style.STROKE);
        mStickerPaint.setStrokeWidth(outLineWidth);
        mStickerPaint.setColor(outLineColor);

        Paint mBtnPaint = new Paint();
        mBtnPaint.setAntiAlias(true);
        mBtnPaint.setColor(Color.BLACK);
        mBtnPaint.setStyle(Paint.Style.FILL);

        mStickers = new ArrayList<>();

        btnDeleteBitmap = BitmapFactory.decodeResource(getResources(), closeIcon);
        btnDeleteBitmap = Bitmap.createScaledBitmap(btnDeleteBitmap, closeSize, closeSize, true);
        btnRotateBitmap = BitmapFactory.decodeResource(getResources(), rotateIcon);
        btnRotateBitmap = Bitmap.createScaledBitmap(btnRotateBitmap, rotateSize, rotateSize, true);

        lastPoint = new PointF();

    }

    public boolean addSticker(@DrawableRes int res) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res);
        return addSticker(bitmap);
    }

    public boolean addSticker(Bitmap stickerBitmap) {
        if (mStickers.size() >= maxStickerCount) {
            return false;
        }
        Sticker bean = new Sticker(stickerBitmap);
        mStickers.add(bean);
        currentSticker = bean;
        invalidate();
        return true;
    }

    public void removeSticker(Sticker sticker) {
        mStickers.remove(sticker);
        invalidate();
    }

    public void clearSticker() {
        mStickers.clear();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStickers(canvas);
    }

    private void drawStickers(Canvas canvas) {
        for (Sticker sticker : mStickers) {
            if (!sticker.isInit()) {
                float imageWidth = imageBeginScale * getMeasuredWidth();
                float imageHeight = imageWidth / sticker.getBitmapScale();
                float minSize = (float) Math.sqrt(imageWidth * imageWidth + imageHeight * imageHeight);
                sticker.setMinStickerSize(minSize * minStickerSizeScale / 2);
                sticker.getMatrix().postScale(imageWidth / sticker.getSrc().getWidth(), imageWidth / sticker.getSrc().getWidth());
                sticker.getMatrix().postTranslate(
                        (getMeasuredWidth() - imageWidth) / 2,
                        (getMeasuredHeight() - imageHeight) / 2);
                sticker.converse();
                sticker.setInit(true);
            }
            canvas.drawBitmap(sticker.getSrc(), sticker.getMatrix(), null);
            if (sticker == currentSticker) {
                canvas.drawPath(sticker.getBoundPath(), mStickerPaint);
                drawBtn(sticker, canvas);
            }
        }
    }

    private void drawBtn(Sticker sticker, Canvas canvas) {
        canvas.drawBitmap(btnDeleteBitmap,
                sticker.getDst()[0] - btnDeleteBitmap.getWidth() / 2,
                sticker.getDst()[1] - btnDeleteBitmap.getHeight() / 2,
                null);
        canvas.drawBitmap(btnRotateBitmap,
                sticker.getDst()[4] - btnRotateBitmap.getWidth() / 2,
                sticker.getDst()[5] - btnRotateBitmap.getHeight() / 2,
                null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float evX = event.getX(0);
        float evY = event.getY(0);
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                midPoint = calculateMidPoint(event);
                oldDistance = StickerUtil.calculateDistance(event);
                oldRotation = StickerUtil.calculateRotation(event);
                if (touchInsideSticker(event.getX(0), event.getY(0))
                        && touchInsideSticker(event.getX(1), event.getY(1))) {
                    state = TouchState.DOUBLE_TOUCH;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (touchInsideDeleteButton(evX, evY)) {
                    state = TouchState.PRESS_DELETE;
                    break;
                }
                if (touchInsideRotateButton(evX, evY)) {
                    state = TouchState.PRESS_SCALE_AND_ROTATE;
                    break;
                }
                if (touchInsideSticker(evX, evY)) {
                    state = TouchState.TOUCHING_INSIDE;
                } else {
                    state = TouchState.TOUCHING_OUTSIDE;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                float dx = evX - lastPoint.x;
                float dy = evY - lastPoint.y;
                if (state == TouchState.PRESS_SCALE_AND_ROTATE) {
                    rotateAndScale(evX, evY);
                }
                if (state == TouchState.DOUBLE_TOUCH) {
                    rotateAndScaleDoubleTouch(event);
                }
                if (state == TouchState.TOUCHING_INSIDE) {
                    translate(dx, dy);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (state == TouchState.PRESS_DELETE && touchInsideDeleteButton(evX, evY)) {
                    mStickers.remove(currentSticker);
                    currentSticker = null;
                    invalidate();
                    break;
                }
                if (state == TouchState.TOUCHING_INSIDE || state == TouchState.PRESS_SCALE_AND_ROTATE) {
                    break;
                }
                if (state == TouchState.TOUCHING_OUTSIDE) {
                    currentSticker = null;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                oldDistance = 0;
                oldRotation = 0;
                break;
        }
        lastPoint.x = evX;
        lastPoint.y = evY;
        return true;
    }

    private void rotateAndScaleDoubleTouch(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return;
        }
        //双手旋转时根据映射出的四个点坐标来判断最小值的临界点
        float centerX = (currentSticker.getDst()[0] + currentSticker.getDst()[4]) / 2;
        float centerY = (currentSticker.getDst()[1] + currentSticker.getDst()[5]) / 2;
        float rightBottomX = currentSticker.getDst()[4];
        float rightBottomY = currentSticker.getDst()[5];

        float pathMeasureLength = getPathMeasureLength(centerX, centerY, rightBottomX, rightBottomY);
        float newDistance = StickerUtil.calculateDistance(event);
        float newRotation = StickerUtil.calculateRotation(event);
        if (oldDistance != 0) {
            Matrix matrix = currentSticker.getMatrix();
            //可以放大  不能缩小
            if ((newDistance - oldDistance) > 0) {
                matrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                        midPoint.y);
            } else if (pathMeasureLength > currentSticker.getMinStickerSize()) {
                matrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                        midPoint.y);
            }
            matrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
            currentSticker.converse();
        }
        oldDistance = newDistance;
        oldRotation = newRotation;
    }

    private void rotateAndScale(float evX, float evY) {
        float[] src = currentSticker.getRotateSrcPts();
        float[] dst = new float[4];
        float centerX = (currentSticker.getDst()[0] + currentSticker.getDst()[4]) / 2;
        float centerY = (currentSticker.getDst()[1] + currentSticker.getDst()[5]) / 2;

        //获取到触摸点到中心点距离 计算到中心点的距离比例得到X和Y的比例  通过相似三角形计算出最终结果
        float pathMeasureLength = getPathMeasureLength(centerX, centerY, evX, evY);
        if (pathMeasureLength < currentSticker.getMinStickerSize()) {
            evX = currentSticker.getMinStickerSize() * (evX - centerX) / pathMeasureLength + centerX;
            evY = currentSticker.getMinStickerSize() * (evY - centerY) / pathMeasureLength + centerY;
        }
        dst[0] = centerX;
        dst[1] = centerY;
        dst[2] = evX;
        dst[3] = evY;
        Matrix matrix = currentSticker.getMatrix();
        matrix.reset();
        //并不是将图片从一组点变成另一组点  而是获取这两个组的点变换的matrix
        matrix.setPolyToPoly(src, 0, dst, 0, 2);
        currentSticker.converse();
    }

    private float getPathMeasureLength(float moveX, float moveY, float lineX, float lineY) {
        Path path = new Path();
        path.moveTo(moveX, moveY);
        path.lineTo(lineX, lineY);
        PathMeasure pathMeasure = new PathMeasure(path, false);
        return pathMeasure.getLength();
    }

    private float oldDistance = 0f;
    private float oldRotation = 0f;
    private PointF midPoint = new PointF();


    protected PointF calculateMidPoint(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            midPoint.set(0, 0);
            return midPoint;
        }
        try {
            float x = (event.getX(0) + event.getX(1)) / 2;
            float y = (event.getY(0) + event.getY(1)) / 2;
            midPoint.set(x, y);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return midPoint;
    }


    private boolean touchInsideRotateButton(float evX, float evY) {
        return currentSticker != null && new RectF(currentSticker.getDst()[4] - btnRotateBitmap.getWidth() / 2, currentSticker.getDst()[5] - btnRotateBitmap.getHeight() / 2, currentSticker.getDst()[4] + btnRotateBitmap.getWidth() / 2, currentSticker.getDst()[5] + btnRotateBitmap.getHeight() / 2).contains(evX, evY);
    }

    private boolean touchInsideDeleteButton(float evX, float evY) {
        return currentSticker != null && new RectF(currentSticker.getDst()[0] - btnDeleteBitmap.getWidth() / 2, currentSticker.getDst()[1] - btnDeleteBitmap.getHeight() / 2, currentSticker.getDst()[0] + btnDeleteBitmap.getWidth() / 2, currentSticker.getDst()[1] + btnDeleteBitmap.getHeight() / 2).contains(evX, evY);
    }

    private void translate(float dx, float dy) {
        if (currentSticker == null) {
            return;
        }
        Matrix matrix = currentSticker.getMatrix();
        matrix.postTranslate(dx, dy);
        currentSticker.converse();
    }

    private boolean touchInsideSticker(float evX, float evY) {
        for (Sticker sticker : mStickers) {
            Region region = new Region();
            region.setPath(sticker.getBoundPath(), new Region(0, 0, getMeasuredWidth(), getMeasuredHeight()));
            if (region.contains((int) evX, (int) evY)) {
                currentSticker = sticker;
                int index = mStickers.indexOf(currentSticker);
                Collections.swap(mStickers, index, mStickers.size() - 1);
                return true;
            }
        }
        return false;
    }

    private enum TouchState {
        TOUCHING_INSIDE, TOUCHING_OUTSIDE, PRESS_DELETE, PRESS_SCALE_AND_ROTATE, DOUBLE_TOUCH;
    }

    public int getMaxStickerCount() {
        return maxStickerCount;
    }

    public void setMaxStickerCount(int maxStickerCount) {
        this.maxStickerCount = maxStickerCount;
    }

    public float getMinStickerSizeScale() {
        return minStickerSizeScale;
    }

    public void setMinStickerSizeScale(float minStickerSizeScale) {
        this.minStickerSizeScale = minStickerSizeScale;
    }

    public float getImageBeginScale() {
        return imageBeginScale;
    }

    public void setImageBeginScale(float imageBeginScale) {
        this.imageBeginScale = imageBeginScale;
    }

    public int getCloseIcon() {
        return closeIcon;
    }

    public void setCloseIcon(int closeIcon) {
        this.closeIcon = closeIcon;
    }

    public int getRotateIcon() {
        return rotateIcon;
    }

    public void setRotateIcon(int rotateIcon) {
        this.rotateIcon = rotateIcon;
    }

    public int getCloseSize() {
        return closeSize;
    }

    public void setCloseSize(int closeSize) {
        this.closeSize = closeSize;
    }

    public int getRotateSize() {
        return rotateSize;
    }

    public void setRotateSize(int rotateSize) {
        this.rotateSize = rotateSize;
    }

    public int getOutLineWidth() {
        return outLineWidth;
    }

    public void setOutLineWidth(int outLineWidth) {
        this.outLineWidth = outLineWidth;
    }

    public int getOutLineColor() {
        return outLineColor;
    }

    public void setOutLineColor(int outLineColor) {
        this.outLineColor = outLineColor;
    }

    public int dip2px(Context c, float dpValue) {
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public Bitmap saveSticker() {
        currentSticker = null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }
}
