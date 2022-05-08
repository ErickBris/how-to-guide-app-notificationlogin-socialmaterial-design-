package com.ristana.how_to.entity;


import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by hsn on 05/04/2017.
 */

public class GuideORM extends SugarRecord{

    private Long num;

    private String title;

    private String category;

    private String content;

    private Boolean comment;

    private String image;

    private String created;


    public GuideORM() {

    }

    public GuideORM(Long num, String title, String content, String image, String created,  String category,  Boolean comment) {
        this.num = num;
        this.title = title;
        this.content = content;
        this.image = image;
        this.created = created;
        this.category = category;
        this.comment = comment;
    }

    public GuideORM(Guide a) {
        this.num = Long.valueOf(a.getId());
        this.title = a.getTitle();
        this.content = a.getContent();
        this.image = a.getImage();
        this.created = a.getCreated();
        this.category = a.getCategory();
        this.comment = a.getComment();
    }


    public Long getNum() {
        return num;
    }

    public void setNum(Long id) {
        this.num = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }


}
