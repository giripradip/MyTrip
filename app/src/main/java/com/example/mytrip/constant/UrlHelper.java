package com.example.mytrip.constant;

public class UrlHelper {

    public static final String BASE_URL = "https://www.example.com/"; // fake base url

    public static String generateImageUrl(String imgPath) {

        return BASE_URL.concat(imgPath);
    }
}
