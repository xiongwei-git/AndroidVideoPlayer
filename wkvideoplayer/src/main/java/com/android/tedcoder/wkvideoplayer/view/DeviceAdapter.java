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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.tedcoder.wkvideoplayer.R;

/**
 * Created by Ted on 2015/10/28.
 */
class DeviceAdapter extends BaseAdapter {
    private Context mContext;
    private SuperVideoPlayer mSuperVideoPlayer;

    public DeviceAdapter(SuperVideoPlayer superVideoPlayer,Context context) {
        mSuperVideoPlayer = superVideoPlayer;
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mSuperVideoPlayer.getDevices() == null) {
            return 0;
        } else {
            return mSuperVideoPlayer.getDevices().size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSuperVideoPlayer.getDevices() != null) {
            return mSuperVideoPlayer.getDevices().get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext,R.layout.item_lv_main, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name_item = (TextView) convertView
                .findViewById(R.id.tv_name_item);
        holder.tv_name_item.setText(mSuperVideoPlayer.getDevices().get(position)
                .getFriendlyName());
        return convertView;
    }

    static class ViewHolder {
        private TextView tv_name_item;
    }
}
