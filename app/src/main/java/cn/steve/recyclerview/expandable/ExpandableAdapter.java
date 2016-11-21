package cn.steve.recyclerview.expandable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.steve.study.R;

/**
 * Created by yantinggeng on 2016/11/18.
 */

public class ExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int typeSection = 11;
    private int typeSectionItem = 22;
    private int typeExpandItem = 33;

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<ExpandAdapterItem> data;
    private SectionItemClickListener sectionItemClickListener;
    private ExpandStatusListener expandStatusListener;

    public ExpandableAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    public void setSectionItemClickListener(SectionItemClickListener sectionItemClickListener) {
        this.sectionItemClickListener = sectionItemClickListener;
    }

    public void setExpandStatusListener(ExpandStatusListener expandStatusListener) {
        this.expandStatusListener = expandStatusListener;
    }

    public ArrayList<ExpandAdapterItem> getData() {
        return data;
    }

    public void setData(ArrayList<ExpandAdapterItem> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == typeSection) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expandable_section, parent, false);
            viewHolder = new SectionViewHolder(view);
        }
        if (viewType == typeSectionItem) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expandable_sectionitem, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    SectionItemBean expandAdapterItem = (SectionItemBean) data.get(position);
                    sectionItemClickListener.onSelected(expandAdapterItem);
                }
            });
            viewHolder = new SectionItemViewHolder(view);
        }
        if (viewType == typeExpandItem) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expandable_expanditem, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    ExpandBean expandBean = (ExpandBean) data.get(position);
                    expandStatusListener.onChangedStatus(expandBean);
                }
            });
            viewHolder = new ExpandItemViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SectionViewHolder) {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            SectionBean sectionBean = (SectionBean) data.get(position);
            // TODO: 2016/11/21 icon not show
            sectionViewHolder.date.setText(sectionBean.getDate());
            sectionViewHolder.airline.setText(sectionBean.getAirline());
            sectionViewHolder.flight.setText(sectionBean.getFlight());
            sectionViewHolder.airplane.setText(sectionBean.getAirplane());
            sectionViewHolder.startTime.setText(sectionBean.getOriginTime());
            sectionViewHolder.startPort.setText(sectionBean.getOriginAirport());
            sectionViewHolder.needTime.setText(sectionBean.getNeedTime());
            sectionViewHolder.middleStop.setText(sectionBean.getMiddleStation());
            sectionViewHolder.endTime.setText(sectionBean.getDestTime());
            sectionViewHolder.destPort.setText(sectionBean.getDestAirport());
        }
        if (holder instanceof SectionItemViewHolder) {
            SectionItemViewHolder sectionItemViewHolder = (SectionItemViewHolder) holder;
            SectionItemBean itemBean = (SectionItemBean) data.get(position);
            sectionItemViewHolder.airClass.setText(itemBean.getAirClass());
            sectionItemViewHolder.ticketPrice.setText(itemBean.getPrice() + "");
            sectionItemViewHolder.stock.setText("剩余(" + itemBean.getStock() + "张)");
            sectionItemViewHolder.selectStatus.setSelected(itemBean.isSelected());
        }
        if (holder instanceof ExpandItemViewHolder) {
            ExpandItemViewHolder expandItemHolder = (ExpandItemViewHolder) holder;
            ExpandBean expandBean = (ExpandBean) data.get(position);
            expandItemHolder.expandStatus.setText(expandBean.getName());
            expandItemHolder.expandIcon.setImageResource(expandBean.isExpanded() ? R.drawable.arrow_up : R.drawable.arrow_down);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        ExpandAdapterItem item = data.get(position);
        if (item instanceof SectionBean) {
            return typeSection;
        }
        if (item instanceof SectionItemBean) {
            return typeSectionItem;
        }
        if (item instanceof ExpandBean) {
            return typeExpandItem;
        }
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public ImageView airlineIcon;
        public TextView date, airline, flight, airplane, startTime, startPort, endTime, destPort, needTime, middleStop;

        public SectionViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            airlineIcon = (ImageView) view.findViewById(R.id.airlineIcon);
            airline = (TextView) view.findViewById(R.id.airline);
            flight = (TextView) view.findViewById(R.id.flight);
            airplane = (TextView) view.findViewById(R.id.airplane);
            startTime = (TextView) view.findViewById(R.id.startTime);
            startPort = (TextView) view.findViewById(R.id.startPort);
            endTime = (TextView) view.findViewById(R.id.endTime);
            destPort = (TextView) view.findViewById(R.id.destPort);
            needTime = (TextView) view.findViewById(R.id.needTime);
            middleStop = (TextView) view.findViewById(R.id.middleStop);
        }
    }

    public static class SectionItemViewHolder extends RecyclerView.ViewHolder {

        public TextView airClass, ticketPrice, stock;
        public ImageView selectStatus;

        public SectionItemViewHolder(View view) {
            super(view);
            airClass = (TextView) view.findViewById(R.id.airClass);
            ticketPrice = (TextView) view.findViewById(R.id.ticketPrice);
            stock = (TextView) view.findViewById(R.id.stock);
            selectStatus = (ImageView) view.findViewById(R.id.selectStatus);
        }
    }

    public static class ExpandItemViewHolder extends RecyclerView.ViewHolder {

        public TextView expandStatus;
        public ImageView expandIcon;

        public ExpandItemViewHolder(View view) {
            super(view);
            expandStatus = (TextView) view.findViewById(R.id.expandStatus);
            expandIcon = (ImageView) view.findViewById(R.id.expandIcon);
        }
    }

}
