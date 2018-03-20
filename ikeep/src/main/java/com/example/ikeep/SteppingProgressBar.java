package com.demoapplication.ui;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by Administrator on 2016/10/4.
 * Function:
 */
public class SteppingProgressBar extends View {
    private Paint mPaint;
    private int mMaxWidth;
    private float mPercent = 0;

    private boolean mIsProgressPause;
    private boolean mAddTimeStamp;

    private List<Rect> mTimeStampPosition = new ArrayList<Rect>();
    private List<Float> mTimeStampPercent = new ArrayList<Float>();

    private final int STATE_NORMAL = 1;
    private final int STATE_DELETE_PREPARE = 2;
    private final int STATE_DELETE_DONE = 3;
    private int mState = STATE_NORMAL;

    private int MAX_PERCENT = 100;
    private final static int PROGRESS_TEXT_SIZE = 56;
    private final static int PROGRESS_SCALE = 1000;
    private final static int PROGRESS_BACKGROUND_COLOR = Color.parseColor("#66030e18");
    private final static int PROGRESS_PASSED_COLOR = Color.parseColor("#7cb855");
    private final static int PROGRESS_STAMP_COLOR = Color.parseColor("#4a7b17");
    private final static int PROGRESS_TEXT_COLOR = Color.parseColor("#ff0000");
    private final static int PROGRESS_DELETING_COLOR = Color.parseColor("#3c6e57");

    private SteppingProgressBarCallbackListener mSteppingProgressBarCallbackListener = null;

    public void setOnDeleteCallbackListener(SteppingProgressBarCallbackListener listener) {
        this.mSteppingProgressBarCallbackListener = listener;
    }

    public interface SteppingProgressBarCallbackListener {
        /* 删除完成的回调 */
        public void deleteDone(float percent);
    }

    public SteppingProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        mPaint = new Paint();

        mPaint.setAlpha(255);
        mPaint.setStyle(Style.FILL);
        mPaint.setDither(true);  //防抖动
        mPaint.setAntiAlias(true); //放锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取尺寸
        int size = MeasureSpec.getSize(heightMeasureSpec);
        size = (int)(size + PROGRESS_TEXT_SIZE + 10);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制底色
        mPaint.setColor(PROGRESS_BACKGROUND_COLOR);
        float width = mMaxWidth = getWidth();
        float height = getHeight() - PROGRESS_TEXT_SIZE - 10;
        canvas.drawRect(0, 0, width, height, mPaint);

        //绘制进度色
        mPaint.setColor(PROGRESS_PASSED_COLOR);
        width = (float) mPercent / (float) MAX_PERCENT * width;
        canvas.drawRect(0, 0, width, height, mPaint);

        //在下次进度条重新开始时，计算上次的分隔条
        if (mAddTimeStamp && mState == STATE_NORMAL && !mIsProgressPause) {
            mAddTimeStamp = false;
            Rect mRect = new Rect();
            mRect.left = (int) (width - 4);
            mRect.top = 0;
            mRect.right = (int) width;
            mRect.bottom = (int) height;
            mTimeStampPosition.add(mRect);
            mTimeStampPercent.add(mPercent);
        }

        //绘制所有分隔条
        for (Rect mRect : mTimeStampPosition) {
            mPaint.setColor(PROGRESS_STAMP_COLOR);
            canvas.drawRect(mRect, mPaint);
        }

        //绘制进度值文本
        mPaint.setStrokeWidth(0);
        mPaint.setColor(PROGRESS_TEXT_COLOR);
        mPaint.setTextSize(PROGRESS_TEXT_SIZE);
        mPaint.setTypeface(Typeface.DEFAULT);
        float textWidth = mPaint.measureText(mPercent / PROGRESS_SCALE + "s");
        if (mPercent != 0) {
            if (width + textWidth > mMaxWidth) {
                canvas.drawText(mPercent / PROGRESS_SCALE + "s", (width - textWidth), height + PROGRESS_TEXT_SIZE, mPaint);
            } else {
                canvas.drawText(mPercent / PROGRESS_SCALE + "s", width, height + PROGRESS_TEXT_SIZE, mPaint);
            }
        }

        if (mState == STATE_DELETE_DONE) {
            mState = STATE_NORMAL;
            //当删除完成时，准备绘制本次分隔条，当percent为0的时候不绘制
            if (mPercent != 0) {
                this.mAddTimeStamp = true;
            }
        } else if (mState == STATE_DELETE_PREPARE) {
            //当准备删除时，将上段时间内的进度条变色
            mPaint.setColor(PROGRESS_DELETING_COLOR);
            int left;
            if (mTimeStampPosition != null && mTimeStampPosition.size() > 0) {
                left = mTimeStampPosition.get(mTimeStampPosition.size() -1).right;
            } else {
                left = 0;
            }

            canvas.drawRect(left, 0, width, height, mPaint);
        }
    }

    /**
     * 进度条的最大值
     * @param maxPercent
     */
    public void setMax(int maxPercent) {
        this.MAX_PERCENT = maxPercent;
    }

    /**
     * 当前进度
     * @return
     */
    public float getProgress() {
        return mPercent;
    }

    /**
     * 设置当前进度
     * @param percent
     */
    public void setProgress(float percent) {
        if (percent < 0) {
            return;
        }

        if (percent >= MAX_PERCENT) {
            percent = MAX_PERCENT;
        }

        this.mPercent = percent;
        this.mState = STATE_NORMAL;
        this.mIsProgressPause = false;

        invalidate();
    }

    /**
     * 设置视频录制中间的断点时间戳
     * @param timeStamp
     */
    public void setTimeStamp(boolean timeStamp) {
        this.mIsProgressPause = true;
        this.mAddTimeStamp = timeStamp;
    }

    /**
     * 准备删除前一段进度
     */
    public void deleteLastStepPrepare() {
        if (mState == STATE_DELETE_PREPARE) {
            return;
        }

        mState = STATE_DELETE_PREPARE;
        invalidate();
    }

    /**
     * 确认删除前一段进度
     */
    public void deleteLastStep() {
        if (mState != STATE_DELETE_PREPARE) {
            deleteLastStepPrepare();
            return;
        }

        if (mTimeStampPercent != null && mTimeStampPercent.size() > 0) {
            this.mPercent = mTimeStampPercent.remove(mTimeStampPercent.size() - 1);
        } else {
            this.mPercent = 0;
        }
        this.mAddTimeStamp = false;
        mState = STATE_DELETE_DONE;

        //操作断点
        if (mTimeStampPosition != null && mTimeStampPosition.size() > 0) {
            mTimeStampPosition.remove(mTimeStampPosition.size() - 1);
        }

        if (mSteppingProgressBarCallbackListener != null) {
            mSteppingProgressBarCallbackListener.deleteDone(this.mPercent);
        }

        invalidate();
    }

    /**
     * 取消删除前一段进度
     */
    public void deleteLastStepCancel() {
        if (mState == STATE_NORMAL) {
            return;
        }
        mState = STATE_DELETE_DONE;
        invalidate();
    }

    /**
     * 重置进度条
     */
    public void reset() {
        this.mPercent = 0;
        this.mAddTimeStamp = false;
        mTimeStampPosition.clear();
        mTimeStampPercent.clear();

        invalidate();
    }
}
