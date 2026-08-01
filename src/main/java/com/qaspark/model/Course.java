package com.qaspark.model;

import java.util.List;

public class Course {

    private String id;
    private String title;
    private String description;
    private String price;
    private String badge;
    private String cardColor;
    private String icon;
    private List<String> topics;

    public Course() {}

    public Course(String id, String title, String description, String price, String badge, String cardColor, String icon, List<String> topics) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.badge = badge;
        this.cardColor = cardColor;
        this.icon = icon;
        this.topics = topics;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public String getCardColor() { return cardColor; }
    public void setCardColor(String cardColor) { this.cardColor = cardColor; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }
}
