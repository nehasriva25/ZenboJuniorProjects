package com.example.healthreadingsdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


// This is specifically to present the list of results of measurements in each Measurement card UI
public class ResultsView extends ListView {


        public ResultsView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ResultsView(Context context) {
            super(context);
        }

        public ResultsView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }


}
