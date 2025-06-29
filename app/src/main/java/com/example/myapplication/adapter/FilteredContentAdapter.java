package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.FilteredContent;
import java.util.List;

public class FilteredContentAdapter extends RecyclerView.Adapter<FilteredContentAdapter.ViewHolder> {
    private List<FilteredContent> contentList;

    public FilteredContentAdapter(List<FilteredContent> contentList) {
        this.contentList = contentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filtered_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilteredContent content = contentList.get(position);
        holder.titleText.setText(content.getTitle());
        holder.sourceText.setText(content.getSource());
        holder.filterTypeText.setText(content.getFilterType());
        holder.timestampText.setText(content.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public void updateData(List<FilteredContent> newContentList) {
        this.contentList = newContentList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView sourceText;
        TextView filterTypeText;
        TextView timestampText;

        ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.title_text);
            sourceText = view.findViewById(R.id.source_text);
            filterTypeText = view.findViewById(R.id.filter_type_text);
            timestampText = view.findViewById(R.id.timestamp_text);
        }
    }
} 