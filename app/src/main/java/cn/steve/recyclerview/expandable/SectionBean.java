package cn.steve.recyclerview.expandable;

/**
 * Created by yantinggeng on 2016/11/18.
 */

public class SectionBean extends ExpandAdapterItem {

    private String date;
    private String airlineIcon;
    private String airline;
    private String flight;
    private String airplane;
    private String originTime;
    private String destTime;
    private String originAirport;
    private String destAirport;
    private String middleStation;
    private String needTime;
    private boolean isExpanded;

    public String getNeedTime() {
        return needTime;
    }

    public void setNeedTime(String needTime) {
        this.needTime = needTime;
    }

    public String getMiddleStation() {
        return middleStation;
    }

    public void setMiddleStation(String middleStation) {
        this.middleStation = middleStation;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getAirlineIcon() {
        return airlineIcon;
    }

    public void setAirlineIcon(String airlineIcon) {
        this.airlineIcon = airlineIcon;
    }

    public String getAirplane() {
        return airplane;
    }

    public void setAirplane(String airplane) {
        this.airplane = airplane;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDestAirport() {
        return destAirport;
    }

    public void setDestAirport(String destAirport) {
        this.destAirport = destAirport;
    }

    public String getDestTime() {
        return destTime;
    }

    public void setDestTime(String destTime) {
        this.destTime = destTime;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }

    public String getOriginTime() {
        return originTime;
    }

    public void setOriginTime(String originTime) {
        this.originTime = originTime;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public boolean isGroup() {
        return true;
    }


}
