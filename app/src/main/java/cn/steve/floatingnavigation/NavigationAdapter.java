package cn.steve.floatingnavigation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.steve.study.R;


/**
 * Created by SteveYan on 2017/6/20.
 */

class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationHolder> {

    private ArrayList<NavigationItem> data = new ArrayList<>();
    private OnRecyclerViewItemClickListener itemClickListener;
    private RecyclerView recyclerView;

    public NavigationAdapter(ArrayList<NavigationItem> data, RecyclerView recyclerView) {
        this.data = data;
        this.recyclerView = recyclerView;
    }

    public void setItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public NavigationHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_popwindow_adapter_item, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener == null) {
                    return;
                }
                itemClickListener.onItemClick(data.get(recyclerView.getChildAdapterPosition(v)));
            }
        });
        return new NavigationHolder(view);
    }

    @Override
    public void onBindViewHolder(NavigationHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    interface OnRecyclerViewItemClickListener {

        void onItemClick(NavigationItem data);
    }

    static class NavigationHolder extends RecyclerView.ViewHolder {

        private TextView day;
        private TextView title;
        private TextView scenes;
        private View line_normal;

        public NavigationHolder(View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.day);
            title = (TextView) itemView.findViewById(R.id.title);
            scenes = (TextView) itemView.findViewById(R.id.scenes);
            line_normal = itemView.findViewById(R.id.line_normal);
        }


        public void bindData(NavigationItem item) {
            day.setText(item.getDay());
            title.setText(item.getTitle());
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> scenic = item.getScenic();
            int size = scenic.size();
            for (int i = 0; i < size; i++) {
                String s = scenic.get(i);
                stringBuilder.append(s);
                if (i < size - 1) {
                    stringBuilder.append("\n");
                }
            }
            scenes.setText(stringBuilder.toString());
            line_normal.setVisibility(item.isLast() ? View.GONE : View.VISIBLE);
        }
    }

    static class NavigationItem {

        private String day;
        private String title;
        private ArrayList<String> scenic;
        private boolean last;

        public NavigationItem(String day, String title) {
            this.day = day;
            this.title = title;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public String getDay() {
            return day;
        }

        public String getTitle() {
            return title;
        }

        public ArrayList<String> getScenic() {
            return scenic;
        }

        public void setScenic(ArrayList<String> scenic) {
            this.scenic = scenic;
        }

    }
}
