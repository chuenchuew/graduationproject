package com.example.admin.graduationproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by admin on 2016/11/13.
 */
public class ScrollviewListview extends ListView {


        public ScrollviewListview(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        public ScrollviewListview(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
        }

        public ScrollviewListview(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //根據模式計算每個Child的高度和寬度
            int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    View.MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }

}
