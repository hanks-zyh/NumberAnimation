package xyz.hanks.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Number View with animation to display a number
 */
public class NumberView extends View {
    private String mNumberString = "";

    private TextPaint mTextPaint;

    private float mTextWidth;
    private float mTextHeight;
    private float mTextSize = 0;    // textSize
    private float mCharWidth;       // character space
    private float mExtraSpace;      // character space

    private int ANIM_TIME = 1000;
    private int mNumberColor = Color.BLACK; // textColor

    private List<ValueAnimator> animatorList = new ArrayList<>();
    private List<Integer> startNumber = new ArrayList<>();

    public NumberView(Context context) {
        super(context);
        init(null, 0);
    }

    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NumberView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NumberView, defStyle, 0);
        mNumberColor = a.getColor(
                R.styleable.NumberView_numberColor,
                mNumberColor);
        mExtraSpace = a.getDimension(
                R.styleable.NumberView_charSpace,
                mExtraSpace);
        mTextSize = a.getDimension(
                R.styleable.NumberView_textSize,
                mTextSize);
        if (a.hasValue(R.styleable.NumberView_number)) {
            mNumberString = a.getString(
                    R.styleable.NumberView_number);
        }
        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mNumberColor);
        mTextWidth = mTextPaint.measureText(mNumberString);
        mCharWidth = mTextWidth / mNumberString.length();

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent;
        startNumber.clear();
        for (int i = 0; i < mNumberString.length(); i++) {
            int num = Integer.parseInt("" + mNumberString.charAt(i));
            startNumber.add(i, calcNum(num, 5));
        }
    }

    private int calcNum(int num, int i) {
        // num 0 ~ 9
        int result = 0;
        if (num + i > 9) {
            result = num + i - 10;
        } else if (num + i < 0) {
            result = num + i + 10;
        } else {
            result = num + i;
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) mTextSize;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        Log.e(".......", "paddingTop = " + paddingTop
                + ",paddingBottom = " + paddingBottom
                + ",contentHeight = " + contentHeight
                + ",mTextSize = " + mTextSize
                + ",mTextHeight = " + mTextHeight
                + ",getHeight() = " + getHeight());
        float startX = paddingLeft + (contentWidth - mTextWidth) / 2;
        startX -= mExtraSpace * (startNumber.size() - 1) / 2;
        float startY = paddingTop + (contentHeight + mTextHeight) / 2;

        // progress : 0f ~ 1f
        // dy : 0 ~ (mTextHeight * mNumberString.length()-1)

        for (int i = 0; i < startNumber.size(); i++) {
            float progress = 1;
            if (i < animatorList.size()) {
                progress = (float) animatorList.get(i).getAnimatedValue();
            }
            int dy = (int) ((mTextSize * 5) * progress);
            startY = mTextSize - mTextHeight / 2 - dy;
            Integer num = startNumber.get(i);
            for (int j = 0; j < 6; j++) {
                String c = "" + calcNum(num, -j);
                canvas.drawText(c,
                        startX,
                        startY,
                        mTextPaint);
                startY += mTextSize;

            }
            startX += mCharWidth + mExtraSpace;
        }

        boolean end = true;
        for (ValueAnimator valueAnimator : animatorList) {
            if (valueAnimator.isRunning()) {
                end = false;
            }
        }
        if (!end) {
            invalidate();
        }
    }

    public void play() {
        for (ValueAnimator valueAnimator : animatorList) {
            valueAnimator.cancel();
        }
        animatorList.clear();
        for (int i = 0; i < mNumberString.length(); i++) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(ANIM_TIME + 100 * i);
            valueAnimator.start();
            animatorList.add(valueAnimator);
        }
        invalidate();
    }

    /**
     * return the number
     *
     * @return the number
     */
    public int getNumber() {
        return Integer.parseInt(mNumberString);
    }

    /**
     * set the number to show
     * @param number the number to show
     */
    public void setNumberText(int number) {
        mNumberString = String.valueOf(number).trim();
        invalidateTextPaintAndMeasurements();
    }

    /**
     * number color
     *
     * @return number color
     */
    public int getTextColor() {
        return mNumberColor;
    }

    /**
     * Sets the number color
     *
     * @param numberColor the number color
     */
    public void setTextColor(int numberColor) {
        mNumberColor = numberColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the number size
     *
     * @return the number size
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Sets the  the number size.
     *
     * @param exampleDimension the number size to use.
     */
    public void setTextSize(float exampleDimension) {
        mTextSize = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

}
