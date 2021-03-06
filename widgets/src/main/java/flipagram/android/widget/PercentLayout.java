/*
 * Copyright (C) 2015 Flipagram, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flipagram.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import flipagram.android.widgets.R;

/**
 * A layout that lets you specify locations (x/y coordinates) of its
 * children as a float that ranges between 0 and 1 where 0 is the top/left and 1 is bottom/right.
 */
public class PercentLayout extends ViewGroup {
    public PercentLayout(Context context) {
        this(context, null);
    }

    public PercentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * and with the coordinates (0, 0).
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                PercentLayout.LayoutParams lp = (PercentLayout.LayoutParams) child.getLayoutParams();

                final int childWidthMeasureSpec = getChildMeasureSpec(
                    widthSpec,
                    getPaddingLeft() + getPaddingRight(),
                    lp.width,
                    lp.wide);

                final int childHeightMeasureSpec = getChildMeasureSpec(
                    heightSpec,
                    getPaddingTop() + getPaddingBottom(),
                    lp.height,
                    lp.high);

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
        int lockedWidth=MeasureSpec.getSize(widthSpec);
        int lockedHeight=MeasureSpec.getSize(heightSpec);

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(lockedWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(lockedHeight, MeasureSpec.EXACTLY));

    }

    public static int getChildMeasureSpec(int spec, int padding, int childDimension, float pct) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);

        int size = Math.max(0, specSize - padding);

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
            // Parent has imposed an exact size on us
            case MeasureSpec.EXACTLY:
                if (childDimension == 0) {
                    resultSize = (int)(size * pct);
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension > 0) {
                    resultSize = childDimension;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size. So be it.
                    resultSize = size;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = MeasureSpec.AT_MOST;
                }
                break;

            // Parent has imposed a maximum size on us
            case MeasureSpec.AT_MOST:
                if (childDimension == 0) {
                    resultSize = (int)(size * pct);
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension > 0) {
                    // Child wants a specific size... so be it
                    resultSize = childDimension;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size, but our size is not fixed.
                    // Constrain child to not be bigger than us.
                    resultSize = size;
                    resultMode = MeasureSpec.AT_MOST;
                } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = MeasureSpec.AT_MOST;
                }
                break;

            // Parent asked to see how big we want to be
            case MeasureSpec.UNSPECIFIED:
                if (childDimension == 0) {
                    resultSize = (int)(size * pct);
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension > 0) {
                    // Child wants a specific size... let him have it
                    resultSize = childDimension;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size... find out how big it should
                    // be
                    resultSize = 0;
                    resultMode = MeasureSpec.UNSPECIFIED;
                } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size.... find out how
                    // big it should be
                    resultSize = 0;
                    resultMode = MeasureSpec.UNSPECIFIED;
                }
                break;
        }
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        int childLeft=0;
        int childTop=0;
        int childRight=0;
        int childBottom=0;

        final int paddingTop = getPaddingTop();
        final int paddingLeft = getPaddingLeft();
        final int verticalPadding = paddingTop + getPaddingBottom();
        final int horizontalPadding = getPaddingLeft() + getPaddingRight();

        final int containerVerticalPixels = getMeasuredHeight() - verticalPadding;
        final int containerHorizontalPixels = getMeasuredWidth() - horizontalPadding;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                PercentLayout.LayoutParams lp = (PercentLayout.LayoutParams) child.getLayoutParams();

                final int x = (int) (lp.x * containerHorizontalPixels);
                final int y = (int) (lp.y * containerVerticalPixels);

                final int childVerticalPixels = child.getMeasuredHeight()!=0?
                    child.getMeasuredHeight():
                    (int)(containerVerticalPixels * lp.high);

                final int childHorizontalPixels = child.getMeasuredWidth()!=0?
                    child.getMeasuredWidth():
                    (int)(containerHorizontalPixels * lp.wide);

                switch(lp.gravity & Gravity.VERTICAL_GRAVITY_MASK){
                    case Gravity.BOTTOM:
                        childTop = paddingTop + y - childVerticalPixels;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = paddingTop + y - childVerticalPixels/2;
                        break;
                    case Gravity.TOP:
                    default:
                        childTop = paddingTop + y;
                }
                childBottom = childTop + childVerticalPixels;

                switch(lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK){
                    case Gravity.RIGHT:
                        childLeft = paddingLeft + x - childHorizontalPixels;
                        break;
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = paddingLeft + x - childHorizontalPixels/2;
                        break;
                    case Gravity.LEFT:
                    default:
                        childLeft = paddingLeft + x;
                }
                childRight = childLeft + childHorizontalPixels;

                child.layout(childLeft, childTop, childRight, childBottom);
            }
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PercentLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof PercentLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Per-child layout information associated with PercentLayout.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        /**
         * The horizontal (X) percentage of the left side of the child
         */
        public float x;
        /**
         * The vertical (Y) percentage of the top of the child
         */
        public float y;
        /**
         * The horizontal width percentage
         */
        public float wide;
        /**
         * The vertical height percentage
         */
        public float high;
        public int gravity;
        /**
         * Creates a new set of layout parameters with the specified width,
         * height and location.
         *
         * @param width the width, either {@link #MATCH_PARENT},
        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height the height, either {@link #MATCH_PARENT},
        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param x the X location of the child
         * @param y the Y location of the child
         */
        public LayoutParams(int width, int height, float x, float y) {
            super(width, height);
            this.x = x;
            this.y = y;
        }

        /**
         * Creates a new set of layout parameters. The values are extracted from
         * the supplied attributes set and context. The XML attributes mapped
         * to this set of layout parameters are:
         *
         * <ul>
         *   <li><code>layout_x</code>: the X location of the child</li>
         *   <li><code>layout_y</code>: the Y location of the child</li>
         *   <li>All the XML attributes from
         *   {@link android.view.ViewGroup.LayoutParams}</li>
         * </ul>
         *
         * @param c the application environment
         * @param attrs the set of attributes from which to extract the layout
         *              parameters values
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PercentLayout);
            x = a.getFloat(R.styleable.PercentLayout_percent_x, 0);
            y = a.getFloat(R.styleable.PercentLayout_percent_y, 0);
            wide = a.getFloat(R.styleable.PercentLayout_wide, 0);
            high = a.getFloat(R.styleable.PercentLayout_high, 0);
            gravity = a.getInt(R.styleable.PercentLayout_android_layout_gravity, -1);
            a.recycle();
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
