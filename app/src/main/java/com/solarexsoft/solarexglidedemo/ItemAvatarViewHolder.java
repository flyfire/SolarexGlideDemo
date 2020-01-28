package com.solarexsoft.solarexglidedemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 20:14/2020-01-28
 *    Desc:
 * </pre>
 */

public class ItemAvatarViewHolder extends RecyclerView.ViewHolder {
    TextView tv_position;
    ImageView iv_avatar;

    public ItemAvatarViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_position = itemView.findViewById(R.id.tv_position);
        iv_avatar = itemView.findViewById(R.id.iv_avatar);
    }
}
