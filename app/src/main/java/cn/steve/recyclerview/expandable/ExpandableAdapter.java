package cn.steve.recyclerview.expandable;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.steve.study.R;

/**
 * Created by yantinggeng on 2016/11/18.
 */

public class ExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public GroupViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ItemViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
        }
    }


}
