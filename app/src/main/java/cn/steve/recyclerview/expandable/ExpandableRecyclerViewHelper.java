package cn.steve.recyclerview.expandable;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yantinggeng on 2016/11/18.
 */

public class ExpandableRecyclerViewHelper implements SectionItemClickListener, ExpandStatusListener {

    private Context context;
    private RecyclerView recyclerView;
    private LinkedHashMap<SectionBean, ArrayList<SectionItemBean>> allData = new LinkedHashMap<>();
    private ArrayList<ExpandAdapterItem> mDisplayData = new ArrayList<>();
    private ExpandableAdapter adapter;
    private SectionBean currentSection;
    private OnItemSelectListener clickListener;

    public ExpandableRecyclerViewHelper(Context context, RecyclerView recyclerView, OnItemSelectListener clickListener) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.clickListener = clickListener;
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
        boolean hasFocusSection = false;
        Set<Map.Entry<SectionBean, ArrayList<SectionItemBean>>> entries = allData.entrySet();
        for (Map.Entry<SectionBean, ArrayList<SectionItemBean>> entry : entries) {
            SectionBean key = entry.getKey();
            mDisplayData.add(key);
            ArrayList<SectionItemBean> value = entry.getValue();

            SectionItemBean selected = null;

            if (key.isFocus()) {
                hasFocusSection = true;
                currentSection = key;
            }

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
        if (!hasFocusSection) {
            Iterator<Map.Entry<SectionBean, ArrayList<SectionItemBean>>> iterator = entries.iterator();
            Map.Entry<SectionBean, ArrayList<SectionItemBean>> next = iterator.next();
            SectionBean key = next.getKey();
            key.setFocus(true);
        }
        notifyDataChanged();
    }


    private void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSelected(SectionItemBean item) {
        resetSelected();
        item.setSelected(true);
        if (currentSection != item.getSectionBean()) {
            clearFocus();
            resetExpand();
        }
        item.getSectionBean().setFocus(true);
        clickListener.onSelected(item);
        if (currentSection != item.getSectionBean()) {
            generateList();
        }
    }

    @Override
    public void onChangedStatus(ExpandBean expandBean) {
        clearFocus();
        resetExpand();
        SectionBean section = expandBean.getSectionBean();
        section.setExpanded(!expandBean.isExpanded());
        section.setFocus(true);
        generateList();
    }

    private void resetSelected() {
        Set<Map.Entry<SectionBean, ArrayList<SectionItemBean>>> entries = allData.entrySet();
        for (Map.Entry<SectionBean, ArrayList<SectionItemBean>> entry : entries) {
            ArrayList<SectionItemBean> items = entry.getValue();
            for (SectionItemBean item : items) {
                item.setSelected(false);
            }
        }
    }

    private void clearFocus() {
        Set<Map.Entry<SectionBean, ArrayList<SectionItemBean>>> entries = allData.entrySet();
        for (Map.Entry<SectionBean, ArrayList<SectionItemBean>> entry : entries) {
            SectionBean key = entry.getKey();
            key.setFocus(false);
        }
    }

    private void resetExpand() {
        Set<Map.Entry<SectionBean, ArrayList<SectionItemBean>>> entries = allData.entrySet();
        for (Map.Entry<SectionBean, ArrayList<SectionItemBean>> entry : entries) {
            SectionBean key = entry.getKey();
            key.setExpanded(false);
        }
    }


    public interface OnItemSelectListener {

        public void onSelected(SectionItemBean item);

    }

}
