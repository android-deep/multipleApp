package com.ft.mapp.home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ft.mapp.R;
import com.ft.mapp.home.models.FakeIconModel;

import java.util.List;

public class FakeIconAdapter extends RecyclerView.Adapter<FakeIconAdapter.ViewHolder> {

    private List<FakeIconModel> iconModels;

    public FakeIconAdapter(List<FakeIconModel> iconModels) {
        this.iconModels = iconModels;
    }

    private int preChosenIndex = 0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fake_icon, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FakeIconModel iconModel = iconModels.get(position);
        if (iconModel.getImgDrawable() != null) {
            holder.ivIcon.setImageDrawable(iconModel.getImgDrawable());
        } else {
            holder.ivIcon.setImageResource(iconModel.getImgRes());
        }
        holder.ivStatus.setVisibility(iconModel.isChosen() ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setOnClickListener(v -> {
            if (preChosenIndex == position) {
                return;
            }
            iconModels.get(preChosenIndex).setChosen(false);
            iconModel.setChosen(true);
            preChosenIndex = position;
            notifyDataSetChanged();
            if (onFakeIconChosenListener != null) {
                onFakeIconChosenListener.onFakeChosen(position);
            }

        });
    }

    @Override
    public int getItemCount() {
        return iconModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        ImageView ivStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.item_fake_iv_logo);
            ivStatus = itemView.findViewById(R.id.item_fake_iv_status);

        }
    }

    private OnFakeIconChosenListener onFakeIconChosenListener;

    public void setOnFakeIconChosenListener(OnFakeIconChosenListener onFakeIconChosenListener) {
        this.onFakeIconChosenListener = onFakeIconChosenListener;
    }

    public interface OnFakeIconChosenListener {
        void onFakeChosen(int index);
    }

}
