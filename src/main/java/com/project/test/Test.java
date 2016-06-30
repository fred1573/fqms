package com.project.test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2016/4/5.
 */
public class Test {


    public static void main(String[] args) {
        Set<String> str = new HashSet<>();
        str.add("a");
        str.add("b");
        str.add("c");
        for (String s : str) {
            str.remove(s);
        }
    }
}
