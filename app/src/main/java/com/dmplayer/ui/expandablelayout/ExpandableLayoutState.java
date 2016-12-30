package com.dmplayer.ui.expandablelayout;

import android.support.v7.widget.RecyclerView;

public interface ExpandableLayoutState {
    RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter();
    RecyclerView.LayoutManager getLayoutManager();
}