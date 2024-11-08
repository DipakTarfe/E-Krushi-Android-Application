package com.example.e_krushi.activities;

public class ModelAi {
    private  int imageView;
    private String textView;

    ModelAi(int imageView, String textView){
        this.imageView=imageView;
        this.textView=textView;
    }

    public int getImageView() {
        return imageView;
    }

    public String getTextView() {
        return textView;
    }
}
