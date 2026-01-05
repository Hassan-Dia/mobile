package com.mentorbridge.app.models;

public class Category {
    private int id;
    private String name;
    private String description;
    private String icon;
    private int mentorCount;

    public Category() {}

    public Category(int id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getMentorCount() {
        return mentorCount;
    }

    public void setMentorCount(int mentorCount) {
        this.mentorCount = mentorCount;
    }
}
