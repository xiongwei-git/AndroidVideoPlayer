/*
 *
 * Copyright 2015 TedXiong xiong-wei@hotmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tedcoder.wkvideoplayer.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tedcoder.wkvideoplayer.R;
import com.android.tedcoder.wkvideoplayer.util.DensityUtil;

import java.util.ArrayList;

/**
 * Created by Ted on 2015/9/1.
 * EasySwitcher
 */
public class EasySwitcher extends LinearLayout{
    private final int NORMAL_ICON = R.drawable.shipin_shang;
    private final int SELECT_ICON = R.drawable.shipin_xia;

    private Context mContext;
    private LinearLayout mItemContainer;
    private TextView mSelectItemTxt;
    private ArrayList<String> mAllItemArray;
    private int mDefaultSelection;

    private EasySwitcherCallbackImpl mEasySwitcherCallback;



    public EasySwitcher(Context context) {
        super(context);
        mContext = context;
        mAllItemArray = new ArrayList<>();
        mDefaultSelection = 0;
        initBaseView();
    }

    public EasySwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAllItemArray = new ArrayList<>();
        mDefaultSelection = 0;
        initBaseView();
    }

    public EasySwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mAllItemArray = new ArrayList<>();
        mDefaultSelection = 0;
        initBaseView();
    }

    private OnClickListener mItemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(null != v && v instanceof TextView){
                int position = (int)v.getTag();
                String name = ((TextView)v).getText().toString();
                closeSwitchList();
                mSelectItemTxt.setText(name);
                setSelectItemStyle(false);
                mDefaultSelection = position;
                mEasySwitcherCallback.onSelectItem(position,name);
            }
        }
    };

    private OnClickListener mSelectItemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(closeSwitchList()){
                return;
            }
            mEasySwitcherCallback.onShowList();
            showItemList();
        }
    };

    public void initData(ArrayList<String> allItemArray){
        mAllItemArray.clear();
        mAllItemArray.addAll(allItemArray);
        updateSelectItem(0);
    }

    public void updateSelectItem(int selectPosition){
        if(null == mAllItemArray || selectPosition < 0 || selectPosition >= mAllItemArray.size())return;
        mDefaultSelection = selectPosition;
        mSelectItemTxt.setText(mAllItemArray.get(selectPosition));
        mItemContainer.removeAllViews();
        mSelectItemTxt.setSelected(false);
    }

    /***
     * 关闭弹出的选择列表
     * @return 是否消耗了执行事件。 如果没有，就代表没有列表不需要关闭
     */
    public boolean closeSwitchList(){
        if(mItemContainer.getChildCount() > 0){
            mItemContainer.removeAllViews();
            setSelectItemStyle(false);
            return true;
        }
        return false;
    }


    private void showItemList(){
        setSelectItemStyle(true);
        mItemContainer.removeAllViews();
        for(int i = 0;i < mAllItemArray.size();i++){
            mItemContainer.addView(getItemView(i));
            mItemContainer.addView(getDividerView());
        }
    }

    private void initBaseView(){
        View.inflate(mContext, R.layout.video_easy_switcher_layout, this);
        mItemContainer = (LinearLayout)findViewById(R.id.switcher_item_container);
        mSelectItemTxt = (TextView)findViewById(R.id.switcher_select);
        setSelected(false);
        mSelectItemTxt.setOnClickListener(mSelectItemOnClickListener);
    }

    private void setSelectItemStyle(boolean isSelect){
        mSelectItemTxt.setSelected(isSelect);
        Drawable rightDrawable = mContext.getResources().getDrawable(isSelect?SELECT_ICON:NORMAL_ICON);
        if (null != rightDrawable)
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        mSelectItemTxt.setCompoundDrawables(null, null, rightDrawable, null);
    }

    private View getItemView(int position){
        TextView textView = new TextView(mContext);
        textView.setTextAppearance(mContext, R.style.switcher_item_text_style);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(mContext,35));
        textView.setLayoutParams(layoutParams);
        textView.setSelected(position == mDefaultSelection);
        textView.setGravity(Gravity.CENTER);
        textView.setText(mAllItemArray.get(position));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f);
        textView.setOnClickListener(mItemOnClickListener);
        textView.setTag(position);
        return textView;
    }

    private View getDividerView(){
        View view = new View(mContext);
        view.setBackgroundColor(mContext.getResources().getColor(R.color.divider_color));
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3));
        return view;
    }

    public void setEasySwitcherCallback(EasySwitcherCallbackImpl easySwitcherCallback) {
        mEasySwitcherCallback = easySwitcherCallback;
    }

    public interface EasySwitcherCallbackImpl{
        void onSelectItem(int position,String name);

        void onShowList();
    }
}
