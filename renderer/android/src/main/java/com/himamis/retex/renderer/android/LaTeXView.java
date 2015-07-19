package com.himamis.retex.renderer.android;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.himamis.retex.renderer.android.graphics.ColorA;
import com.himamis.retex.renderer.android.graphics.Graphics2DA;
import com.himamis.retex.renderer.share.ColorUtil;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

public class LaTeXView extends View {

    private TeXFormula mFormula;
    private TeXIcon mTexIcon;
    private TeXFormula.TeXIconBuilder mTexIconBuilder;

    private Graphics2DA mGraphics;

    private String mLatexText = "";
    private float mSize = 20;
    private int mStyle = TeXConstants.STYLE_DISPLAY;
    private Color mForegroundColor = ColorUtil.BLACK;
    private int mBackgroundColor = android.graphics.Color.WHITE;
    private int mType = TeXFormula.SERIF;

    private float mSizeScale;

    public LaTeXView(Context context) {
        super(context);
        mSizeScale = context.getResources().getDisplayMetrics().scaledDensity;
        ensureTeXIconExists();
    }

    public LaTeXView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSizeScale = context.getResources().getDisplayMetrics().scaledDensity;
        readAttributes(context, attrs, 0);
    }

    public LaTeXView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSizeScale = context.getResources().getDisplayMetrics().scaledDensity;
        readAttributes(context, attrs, defStyleAttr);
    }

    private void readAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LaTeXView,
                defStyleAttr, 0);

        try {
            mLatexText = a.getString(R.styleable.LaTeXView_latexText);
            mSize = a.getFloat(R.styleable.LaTeXView_size, 20);
            mStyle = a.getInteger(R.styleable.LaTeXView_style, 0);
            mType = a.getInteger(R.styleable.LaTeXView_type, TeXFormula.SERIF);
            mBackgroundColor = a.getColor(R.styleable.LaTeXView_backgroundColor, android.graphics.Color.WHITE);
            mForegroundColor = new ColorA(a.getColor(R.styleable.LaTeXView_foregroundColor, android.graphics.Color.BLACK));
        } finally {
            a.recycle();
        }
        ensureTeXIconExists();
    }

    /**
     * Sets the LaTeX text of this view. Must be called from the UI thread.
     * @param latexText formula in LaTeX format
     */
    public void setLatexText(String latexText) {
        mLatexText = latexText;
        mFormula = null;
        mTexIconBuilder = null;
        mTexIcon = null;
        ensureTeXIconExists();
        invalidate();
        requestLayout();
    }

    /**
     * Sets the size of the text. Must be called from the UI thread.
     * @param size size
     */
    public void setSize(float size) {
        if (Math.abs(mSize - size) > 0.01) {
            mSize = size;
            mTexIcon = null;
            ensureTeXIconExists();
            invalidate();
            requestLayout();
        }
    }

    /**
     * Sets the style of the LaTeX. Must be called from the UI thread.
     * @param style one of {@link TeXConstants#STYLE_TEXT} , {@link TeXConstants#STYLE_DISPLAY},
     *              {@link TeXConstants#STYLE_SCRIPT} or {@link TeXConstants#STYLE_SCRIPT_SCRIPT}
     */
    public void setStyle(int style) {
        if (mStyle != style) {
            mStyle = style;
            mTexIcon = null;
            ensureTeXIconExists();
            invalidate();
            requestLayout();
        }
    }

    /**
     * Sets the color of the text. Must be called from the UI thread.
     * @param foregroundColor color represented as packed ints
     */
    public void setForegroundColor(int foregroundColor) {
        mForegroundColor = new ColorA(foregroundColor);
        invalidate();
    }

    /**
     * Sets the color of the background. Must be called from the UI thread.
     * @param backgroundColor color represented as packed ints
     */
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        invalidate();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        float newFontScale = newConfig.fontScale;
        if (Math.abs(mSizeScale - newFontScale) > 0.001) {
            mSizeScale = newConfig.fontScale;
            mTexIcon = null;
            ensureTeXIconExists();
            invalidate();
            requestLayout();
        }
    }

    private void ensureTeXIconExists() {
        if (mFormula == null) {
            try {
                mFormula = new TeXFormula(mLatexText);
            } catch (ParseException exception) {
                mFormula = TeXFormula.getPartialTeXFormula(mLatexText);
            }
        }
        if (mTexIconBuilder == null) {
            mTexIconBuilder = mFormula.new TeXIconBuilder();
        }
        if (mTexIcon == null) {
            mTexIconBuilder.setSize(mSize * mSizeScale).setStyle(mStyle).setType(mType);
            mTexIcon = mTexIconBuilder.build();
        }
        mTexIcon.setInsets(new Insets(
                getPaddingTop(),
                getPaddingLeft(),
                getPaddingBottom(),
                getPaddingRight()
        ));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        if (widthSpecMode == MeasureSpec.UNSPECIFIED) {
            measuredWidth = mTexIcon.getIconWidth();
        }
        if (heightSpecMode == MeasureSpec.UNSPECIFIED) {
            measuredHeight = mTexIcon.getIconHeight();
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTexIcon == null) {
            return;
        }

        if (mGraphics == null) {
            mGraphics = new Graphics2DA();
        }
        // draw background
        canvas.drawColor(mBackgroundColor);

        // draw latex
        mGraphics.setCanvas(canvas);
        mTexIcon.setForeground(mForegroundColor);
        mTexIcon.paintIcon(null, mGraphics, 0, 0);
    }
}
