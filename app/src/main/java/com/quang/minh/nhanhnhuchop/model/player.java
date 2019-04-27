package com.quang.minh.nhanhnhuchop.model;

import com.facebook.login.widget.ProfilePictureView;

public class player {
    private int rank;
    private String id;
    private String name;
    private int point;

    public player(int rank, String id, String name, int point) {
        this.rank = rank;
        this.id = id;
        this.name = name;
        this.point = point;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
