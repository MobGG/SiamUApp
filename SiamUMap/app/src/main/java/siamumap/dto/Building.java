package siamumap.dto;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mob on 16-Dec-15.
 */
public class Building {
    protected String buildingNo;
    protected String buildingDescription;
    protected String buildingPicture;
    protected String buildingFloor;
    protected Double lat;
    protected Double lng;

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getBuildingDescription() {
        return buildingDescription;
    }

    public void setBuildingDescription(String buildingDescription) {
        this.buildingDescription = buildingDescription;
    }

    public String getBuildingPicture() {
        return buildingPicture;
    }

    public void setBuildingPicture(String buildingPicture) {
        this.buildingPicture = buildingPicture;
    }

    public String getBuildingFloor() {
        return buildingFloor;
    }

    public void setBuildingFloor(String buildingFloor) {
        this.buildingFloor = buildingFloor;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
