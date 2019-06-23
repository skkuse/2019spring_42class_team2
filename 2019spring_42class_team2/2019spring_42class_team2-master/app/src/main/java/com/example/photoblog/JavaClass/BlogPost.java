package com.example.photoblog.JavaClass;

import java.util.Date;

public class BlogPost extends BlogPostId{

   private String current_UserId,image_url,thumb_url,description, title, price;
   private float height, depth, width;
   private Date post_time;

    public BlogPost() {
    }

    public BlogPost(String current_UserId, String image_url, String thumb_url, String description, String title, String price, Date post_time
    ,float height, float depth, float width) {
        this.current_UserId = current_UserId;
        this.image_url = image_url;
        this.thumb_url = thumb_url;
        this.description = description;
        this.post_time = post_time;
        this.price = price;
        this.title = title;
        this.height = height;
        this.depth = depth;
        this.width = width;
    }

    public BlogPost(String current_UserId, String image_url, String thumb_url,String title, String price, String description) {
        this.current_UserId = current_UserId;
        this.image_url = image_url;
        this.thumb_url = thumb_url;
        this.description = description;
        this.title = title;
        this.price = price;
    }



    public String getCurrent_UserId() {
        return current_UserId;
    }

    public void setCurrent_UserId(String current_UserId) {
        this.current_UserId = current_UserId;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPost_time() {
        return post_time;
    }

    public void setPost_time(Date post_time) {
        this.post_time = post_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
