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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import flipagram.android.widgets.R;

/**
 * TODO: document your custom view class.
 */
public class PrevNextView extends FrameLayout {
    private int nofmText;

    private ImageButton prevButton,nextButton;
    private TextView nofmTextView;
    private int n = -1;
    private int m = -1;

    public PrevNextView(Context context) {
        this(context, null);
    }

    public PrevNextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public PrevNextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.view_prevnext, this);

        nofmTextView = TextView.class.cast(findViewById(R.id.nOfM));
        prevButton   = ImageButton.class.cast(findViewById(R.id.prevButton));
        nextButton   = ImageButton.class.cast(findViewById(R.id.nextButton));

        final TypedArray a = getContext().obtainStyledAttributes(
            attrs, R.styleable.PrevNextView, defStyle, 0);

        nofmText = a.getResourceId(R.styleable.PrevNextView_nofmText,-1);
        prevButton.setImageDrawable(a.getDrawable(R.styleable.PrevNextView_prevDrawable));
        nextButton.setImageDrawable(a.getDrawable(R.styleable.PrevNextView_nextDrawable));
        setBackground(prevButton, a.getDrawable(R.styleable.PrevNextView_prevBackground));
        setBackground(nextButton, a.getDrawable(R.styleable.PrevNextView_nextBackground));
        float verticalPad = a.getDimension(R.styleable.PrevNextView_verticalPad,10.0f);
        prevButton.setPadding(0,(int)verticalPad,0,(int)verticalPad);
        nextButton.setPadding(0,(int)verticalPad,0,(int)verticalPad);
        a.recycle();

        for(ImageButton button: new ImageButton[]{prevButton,nextButton}){
            if (button.getDrawable()==null){
                button.setVisibility(GONE);
            } else {
                button.setEnabled(false);
            }
        }
        if (isInEditMode()){
            return;
        }
        nofmTextView.setVisibility(GONE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackground(View view, Drawable background){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    private void updateUi(){
        if (n>=1 && m>=1){
            if (nofmText>=0){
                nofmTextView.setVisibility(VISIBLE);
                nofmTextView.setText(getContext().getString(nofmText,n,m));
            }
            prevButton.setEnabled(n>1);
            nextButton.setEnabled(n<m);
        }
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
        updateUi();
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
        updateUi();
    }

    public void setPrevOnClickedListener( OnClickListener listener ){
        prevButton.setOnClickListener(listener);
    }

    public void setNextOnClickedListener( OnClickListener listener ){
        nextButton.setOnClickListener(listener);
    }

}
