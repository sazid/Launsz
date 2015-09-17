package com.mohammedsazid.android.launsz.v2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohammedsazid.android.launsz.R;

/**
 * The ViewHolder for the Alphabets
 */
class AppsViewHolder extends RecyclerView.ViewHolder {
    AppsAdapter.AsyncImageLoaderTask imageLoaderTask;
    protected int viewType;
    TextView appLabelTv;
    ImageView appIconIv;

    public AppsViewHolder(View itemView, int viewType) {
        super(itemView);

        this.viewType = viewType;
        appLabelTv = (TextView) itemView.findViewById(R.id.apps_rv_item_label);
        appIconIv = (ImageView) itemView.findViewById(R.id.apps_rv_item_icon);

        itemView.setClickable(true);
    }
}
