package cn.steve.recyclerview.expandable;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yantinggeng on 2016/11/18.
 */

public class ExpandableRecyclerViewHelper implements SectionItemClickListener, ExpandStatusListener {

    private RecyclerView recyclerView;
    private LinkedHashMap<SectionBean, ArrayList<SectionItemBean>> allData = new LinkedHashMap<>();
    private ArrayList<ExpandAdapterItem> mDisplayData = new ArrayList<>();
    private ExpandableAdapter adapter;

    public ExpandableRecyclerViewHelper(Context context, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        adapter = new ExpandableAdapter(context, recyclerView);
        adapter.setData(mDisplayData);
        adapter.setExpandStatusListener(this);
        adapter.setSectionItemClickListener(this);
        //创建默认的线性LayoutManager ;设置成横向的,默认为竖屏的
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setAdapter(adapter);
    }

    public void setAllData(LinkedHashMap<SectionBean, ArrayList<SectionItemBean>> allData) {
        this.allData = allData;
    }

    public void generateList() {
        mDisplayData.clear();
        Set<Map.Entry<SectionBean, ArrayList<SectionItemBean>>> entries = allData.entrySet();
        for (Map.Entry<SectionBean, ArrayList<SectionItemBean>> entry : entries) {
            SectionBean key = entry.getKey();
            mDisplayData.add(key);
            ArrayList<SectionItemBean> value = entry.getValue();

            SectionItemBean selected = null;

            if (key.isExpanded()) {
                for (SectionItemBean sectionItemBean : value) {
                    mDisplayData.add(sectionItemBean);
                }
            } else {
                for (SectionItemBean itemBean : value) {
                    if (itemBean.isSelected()) {
                        selected = itemBean;
                    }
                }
                if (selected == null) {
                    mDisplayData.add(value.get(0));
                } else {
                    mDisplayData.add(selected);
                }
            }

            ExpandBean expandBean = new ExpandBean();
            expandBean.setExpanded(key.isExpanded());
            expandBean.setSectionBean(key);
            mDisplayData.add(expandBean);
        }
        notifyDataChanged();
    }


    private void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSelected(SectionItemBean item) {

    }

    @Override
    public void onChangedStatus(ExpandBean expandBean) {
        expandBean.setExpanded(!expandBean.isExpanded());
        SectionBean section = expandBean.getSectionBean();
        section.setExpanded(expandBean.isExpanded());
        generateList();
    }

}
