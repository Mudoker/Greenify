package com.example.greenify.util;

import androidx.recyclerview.widget.DiffUtil;

import com.example.greenify.model.EventModel;

import java.util.List;

public class EventModelDiffCallback extends DiffUtil.Callback {
    private final List<EventModel> oldList;
    private final List<EventModel> newList;

    public EventModelDiffCallback(List<EventModel> oldList, List<EventModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        // Use a unique identifier for your items
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Check if the content of the items at the specified positions is the same
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
