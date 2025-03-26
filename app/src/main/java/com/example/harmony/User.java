package com.example.harmony;

public class User implements Comparable {
    String userid;
    Double latitude;
    Double longitude;
    String songInfo;

    public User(String userid, Double latitude, Double longitude, String songInfo){
        this.latitude = latitude;
        this.longitude = longitude;
        this.songInfo = songInfo;
        this.userid = userid;
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
