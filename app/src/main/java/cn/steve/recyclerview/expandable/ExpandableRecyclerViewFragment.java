package cn.steve.recyclerview.expandable;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.steve.study.R;


public class ExpandableRecyclerViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private ExpandableRecyclerViewHelper expandableRecyclerViewHelper;

    public ExpandableRecyclerViewFragment() {
    }

    public static ExpandableRecyclerViewFragment newInstance(String param1, String param2) {
        ExpandableRecyclerViewFragment fragment = new ExpandableRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expandable_recycler_view, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        main();
        return view;
    }

    private void main() {
        LinkedHashMap<SectionBean, ArrayList<SectionItemBean>> allData = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            SectionBean sectionBean = new SectionBean();
            sectionBean.setDate("2016-11-11");
            sectionBean.setAirlineIcon("");
            sectionBean.setAirline("中国国航");
            sectionBean.setFlight("CA1800");
            sectionBean.setAirplane("空格A380");
            sectionBean.setOriginTime("12:00");
            sectionBean.setOriginAirport("上海虹桥机场T1");
            sectionBean.setNeedTime("13h");
            sectionBean.setMiddleStation("航天");
            sectionBean.setDestTime("00:20");
            sectionBean.setDestAirport("洛杉矶机场");

            ArrayList<SectionItemBean> beens = new ArrayList<>();
            for (int i1 = 0; i1 < 4; i1++) {
                SectionItemBean sectionItemBean = new SectionItemBean();
                sectionItemBean.setAirClass("经济舱" + i);
                sectionItemBean.setPrice(100);
                sectionItemBean.setStock(i + 3);
                beens.add(sectionItemBean);
            }
            allData.put(sectionBean, beens);
        }
        expandableRecyclerViewHelper = new ExpandableRecyclerViewHelper(getActivity(), recyclerView);
        expandableRecyclerViewHelper.setAllData(allData);
        expandableRecyclerViewHelper.generateList();
    }

}
