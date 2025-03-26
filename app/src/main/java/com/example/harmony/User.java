package com.example.harmony;

public class User implements Comparable {
    String userid;
    Double latitude;
    Double longitude;
    String songInfo;
    boolean isActive;

    public User(String userid, Double latitude, Double longitude, String songInfo, boolean isActive){
        this.latitude = latitude;
        this.longitude = longitude;
        this.songInfo = songInfo;
        this.userid = userid;
        this.isActive = isActive;
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof User))
            return 0;

        if(((User) o).userid == this.userid)
            return 1;

        return 0;
    }
}
