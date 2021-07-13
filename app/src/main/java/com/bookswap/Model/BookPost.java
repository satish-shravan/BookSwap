package com.bookswap.Model;

public class BookPost {
    private String book_name;
    private  String book_desc;
    private  String location;
    private String latitude;
    private  String longitude;
    private  String book_tags;
    private  String book_imageURL;
    private String profile_imageURL;
    private String username;
    private  String date;
    private  String time;
    private  String UID;

    public BookPost()
    {

    }

    public BookPost(String book_name, String book_desc, String location, String latitude, String longitude, String book_tags, String book_imageURL, String profile_imageURL, String username, String date, String time, String UID) {
        this.book_name = book_name;
        this.book_desc = book_desc;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.book_tags = book_tags;
        this.book_imageURL = book_imageURL;
        this.profile_imageURL = profile_imageURL;
        this.username = username;
        this.date = date;
        this.time = time;
        this.UID = UID;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_desc() {
        return book_desc;
    }

    public void setBook_desc(String book_desc) {
        this.book_desc = book_desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getBook_tags() {
        return book_tags;
    }

    public void setBook_tags(String book_tags) {
        this.book_tags = book_tags;
    }

    public String getBook_imageURL() {
        return book_imageURL;
    }

    public void setBook_imageURL(String book_imageURL) {
        this.book_imageURL = book_imageURL;
    }

    public String getProfile_imageURL() {
        return profile_imageURL;
    }

    public void setProfile_imageURL(String profile_imageURL) {
        this.profile_imageURL = profile_imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
