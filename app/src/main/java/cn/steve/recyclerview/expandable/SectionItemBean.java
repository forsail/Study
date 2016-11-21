package cn.steve.recyclerview.expandable;

/**
 * Created by yantinggeng on 2016/11/18.
 */

public class SectionItemBean extends ExpandAdapterItem {

    private String airClass;
    private int stock;
    private int price;
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAirClass() {
        return airClass;
    }

    public void setAirClass(String airClass) {
        this.airClass = airClass;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public boolean isGroup() {
        return false;
    }
}
