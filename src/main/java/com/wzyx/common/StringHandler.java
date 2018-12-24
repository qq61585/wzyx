package com.wzyx.common;


public class StringHandler {
    public static String[] string_split(String s){
        String[] r = s.split("#");
        return r;
    }
    public static void main(String args[]){
        String[] a =string_split("123#456#14##");
        for(int i = 0;i<a.length;i++)
            System.out.println(a[i]);
    }

}
