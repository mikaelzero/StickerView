package miaoyongjun.stickerview;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.support.annotation.NonNull;

/**
 * Author: miaoyongjun
 * Date : 17/8/1
 */

public abstract class Sticker {

    private boolean init;

    private Matrix matrix;

    private float[] srcPts;

    private float[] dst;

    private float[] rotateSrcPts;

    private Path boundPath;

    private float minStickerSize;


    public void init(int width, int height) {
        matrix = new Matrix();

        srcPts = new float[]{0, 0,                                    // 左上
                width, 0,                              // 右上
                width, height,                // 右下
                0, height};
        /*
         * 原始旋转效果的点  图片中心点和图片的右下角的点
         * 触摸时获取到触摸点以及和中心点形成另一组的点
         * 之后通过matrix.setPolyToPoly(src, 0, dst, 0, 2) 方法来获取变换后的matrix
         */
        rotateSrcPts = new float[]{
                width / 2, height / 2,
                width, height,
        };
        dst = new float[8];
        boundPath = new Path();
    }

    public abstract void draw(@NonNull Canvas canvas);

    public abstract int getWidth();

    public abstract int getHeight();

    public float getMinStickerSize() {
        return minStickerSize;
    }

    public void setMinStickerSize(float minStickerSize) {
        this.minStickerSize = minStickerSize;
    }

    boolean isInit() {
        return init;
    }

    void setInit(boolean init) {
        this.init = init;
    }

    Matrix getMatrix() {
        return matrix;
    }

    float getBitmapScale() {
        return getWidth() / (float) getHeight();
    }

    float[] getDst() {
        return dst;
    }

    float[] getRotateSrcPts() {
        return rotateSrcPts;
    }

    void converse() {
        matrix.mapPoints(dst, srcPts);
    }

    Path getBoundPath() {
        boundPath.reset();
        boundPath.moveTo(dst[0], dst[1]);
        boundPath.lineTo(dst[2], dst[3]);
        boundPath.lineTo(dst[4], dst[5]);
        boundPath.lineTo(dst[6], dst[7]);
        boundPath.lineTo(dst[0], dst[1]);
        return boundPath;
    }
}
