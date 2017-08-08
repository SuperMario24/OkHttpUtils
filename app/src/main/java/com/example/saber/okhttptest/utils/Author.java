package com.example.saber.okhttptest.utils;

/**
 * Created by saber on 2017/7/31.
 */

public class Author {

    private boolean is_followed;
    private String editor_notes;
    private String uid;
    private String resume;
    private String url;
    private String avatar;
    private String name;
    private boolean is_special_user;
    private String last_post_time;
    private int n_posts;
    private String alt;
    private String large_avatar;
    private String id;
    private boolean is_auth_author;

    public boolean is_followed() {
        return is_followed;
    }

    public void setIs_followed(boolean is_followed) {
        this.is_followed = is_followed;
    }

    public String getEditor_notes() {
        return editor_notes;
    }

    public void setEditor_notes(String editor_notes) {
        this.editor_notes = editor_notes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean is_special_user() {
        return is_special_user;
    }

    public void setIs_special_user(boolean is_special_user) {
        this.is_special_user = is_special_user;
    }

    public String getLast_post_time() {
        return last_post_time;
    }

    public void setLast_post_time(String last_post_time) {
        this.last_post_time = last_post_time;
    }

    public int getN_posts() {
        return n_posts;
    }

    public void setN_posts(int n_posts) {
        this.n_posts = n_posts;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getLarge_avatar() {
        return large_avatar;
    }

    public void setLarge_avatar(String large_avatar) {
        this.large_avatar = large_avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean is_auth_author() {
        return is_auth_author;
    }

    public void setIs_auth_author(boolean is_auth_author) {
        this.is_auth_author = is_auth_author;
    }
}
