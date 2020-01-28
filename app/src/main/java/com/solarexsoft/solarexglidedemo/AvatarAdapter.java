package com.solarexsoft.solarexglidedemo;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.solarexsoft.solarexglide.facade.Glide;

import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 20:37/2020-01-28
 *    Desc:
 * </pre>
 */

public class AvatarAdapter extends RecyclerView.Adapter<ItemAvatarViewHolder> {
    FragmentActivity activity;
    List<String> urls;

    public AvatarAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ItemAvatarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_avatar, viewGroup, false);
        return new ItemAvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAvatarViewHolder holder, int position) {
        String url = urls.get(position);
        holder.tv_position.setText(String.valueOf(position));
        Glide.with(activity).load(url).into(holder.iv_avatar);
    }

    @Override
    public int getItemCount() {
        if (urls != null) {
            return urls.size();
        }
        return 0;
    }

    public void setData(List<String> data) {
        urls = data;
        notifyDataSetChanged();
    }
}
