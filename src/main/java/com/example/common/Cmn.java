package com.example.common;

public class Cmn {
    @SuppressWarnings("unchecked")
    public static <T> T autoCast(Object prmObj) {
        return (T) prmObj;
    }

}
