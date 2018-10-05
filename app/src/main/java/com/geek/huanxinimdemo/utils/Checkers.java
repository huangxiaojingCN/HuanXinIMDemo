package com.geek.huanxinimdemo.utils;

/**
 * Created by huangxiaojing on 2018/10/5.
 */

public class Checkers {

    public static boolean loginChecker(String useName, String password) {
        if (useName.isEmpty() || password.isEmpty()) {
            return false;
        }

        return true;
    }
}
