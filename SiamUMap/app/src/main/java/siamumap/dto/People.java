package siamumap.dto;

/**
 * Created by Mob on 19-Dec-15.
 */
public class People {
    protected String peopleID;
    protected String peopleName;
    protected String peopleFaculty;
    protected String peopleRoom;
    protected String peopleImage;

    public String getPeopleID() {
        return peopleID;
    }

    public void setPeopleID(String peopleID) {
        this.peopleID = peopleID;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public String getPeopleFaculty() {
        return peopleFaculty;
    }

    public void setPeopleFaculty(String peopleFaculty) {
        this.peopleFaculty = peopleFaculty;
    }

    public String getPeopleRoom() {
        return peopleRoom;
    }

    public void setPeopleRoom(String peopleRoom) {
        this.peopleRoom = peopleRoom;
    }

    public String getPeopleImage() {
        return peopleImage;
    }

    public void setPeopleImage(String peopleImage) {
        this.peopleImage = peopleImage;
    }
}
