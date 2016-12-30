package com.dmplayer.ui.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.models.PlaylistItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpandableLayoutLight extends LinearLayout {
    private static final String SAVE_STATE_EXPANDED = "SAVE_STATE_EXPANDED";
    private static final String SAVE_STATE_LAYOUT = "SAVE_STATE_LAYOUT";

    private RecyclerView content;
    private List<ExpandableItem> items;

    boolean isExpanded;

    public ExpandableLayoutLight(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0);
    }

    public ExpandableLayoutLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ExpandableLayout, defStyleAttr, 0);

        String titleText;
        int headerColor;
        try {
            titleText = a.getString(R.styleable.ExpandableLayout_text_title);
            headerColor = a.getColor(R.styleable.ExpandableLayout_color_header,
                    getResources().getColor(R.color.md_grey_200));
        } finally {
            a.recycle();
        }

        inflate(getContext(), R.layout.expandable_layout_header_light, this);

        content = ButterKnife.findById(this, R.id.exp_content);

        TextView name = ButterKnife.findById(this, R.id.exp_name);
        name.setText(titleText);
        name.setTextColor(headerColor);

        FrameLayout divider = ButterKnife.findById(this, R.id.exp_divider);
        divider.setBackgroundColor(headerColor);
    }

    public void addContent(ExpandableItem item) {
        items.add(item);
    }

    public void showContent() {
        if (onExpandListener != null) {
            onExpandListener.onExpand(this);
        }
        isExpanded = true;
    }

    public void hideContent() {
        if (onExpandListener != null) {
            onExpandListener.onCollapse(this);
        }
        isExpanded = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }


    private OnExpandListener onExpandListener;

    public void setOnExpandListener(OnExpandListener l) {
        this.onExpandListener = l;
    }

    public interface OnExpandListener {
        void onExpand(ExpandableLayoutLight v);
        void onCollapse(ExpandableLayoutLight v);
    }

    class ExpandedContentAdapter extends RecyclerView.Adapter<ExpandedContentAdapter.ViewHolder> {

        private Context context;

        private List<PlaylistItem> items;

        public ExpandedContentAdapter(Context context,List<PlaylistItem> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.expandable_item_large, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PlaylistItem currentModel = items.get(position);

            holder.name.setText(currentModel.getName());
            holder.details.setText(currentModel.getDetails());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.exp_item_large_name) TextView name;
            @BindView(R.id.exp_item_large_details) TextView details;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    class CollapsedContentAdapter extends RecyclerView.Adapter<CollapsedContentAdapter.ViewHolder> {

        private Context context;

        private List<PlaylistItem> items;

        public CollapsedContentAdapter(Context context, List<PlaylistItem> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.expandable_item_small, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //TODO: Drawable with text songs count or first letter in playlist's name
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.exp_item_small_image) ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}