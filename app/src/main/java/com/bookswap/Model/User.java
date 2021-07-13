package com.bookswap.Model;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String is_online;
    private String search;
    private String address;
    private String city;
    private String latitude;
    private String longitude;

    User() {

    }

    public User(String id, String username, String imageURL, String is_online, String search, String address, String city, String latitude, String longitude) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.is_online = is_online;
        this.search = search;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getIs_online() {
        return is_online;
    }

    public void setIs_online(String is_online) {
        this.is_online = is_online;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}