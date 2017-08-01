package miaoyongjun.stickerview;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;


public class Sticker {

    private boolean init;

    private Matrix matrix;

    private Bitmap src;

    private float[] srcPts;

    private float[] dst;

    private float[] rotateSrcPts;

    private Path boundPath;

    private float minStickerSize;


    Sticker(Bitmap src) {
        if (src == null) {
            throw new RuntimeException("the Sticker's src cannot be null");
        }
        this.src = src;
        matrix = new Matrix();

        srcPts = new float[]{0, 0,                                    // 左上
                src.getWidth(), 0,                              // 右上
                src.getWidth(), src.getHeight(),                // 右下
                0, src.getHeight()};
        /*
         * 原始旋转效果的点  图片中心点和图片的右下角的点
         * 触摸时获取到触摸点以及和中心点形成另一组的点
         * 之后通过matrix.setPolyToPoly(src, 0, dst, 0, 2) 方法来获取变换后的matrix
         */
        rotateSrcPts = new float[]{
                src.getWidth() / 2, src.getHeight() / 2,
                src.getWidth(), src.getHeight(),
        };
        dst = new float[8];
        boundPath = new Path();
    }

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

    Bitmap getSrc() {
        return src;
    }

    float getBitmapScale() {
        return src.getWidth() / (float) src.getHeight();
    }

    public void setSrc(Bitmap src) {
        this.src = src;
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
