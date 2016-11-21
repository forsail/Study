package cn.steve.recyclerview.expandable;


/**
 * Created by yantinggeng on 2016/11/21.
 */

public class ExpandBean extends ExpandAdapterItem {

    private String name = "查看更多舱位";
    private boolean isExpanded;
    private SectionBean sectionBean;

    public SectionBean getSectionBean() {
        return sectionBean;
    }

    public void setSectionBean(SectionBean sectionBean) {
        this.sectionBean = sectionBean;
    }

    public String getName() {
        return isExpanded ? "收起" : "查看更多舱位";
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public boolean isGroup() {
        return false;
    }
}

