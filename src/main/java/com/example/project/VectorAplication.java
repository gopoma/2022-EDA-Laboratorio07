package com.example.project;

import java.util.Vector;

public class VectorAplication {
    public static void main(String[] args) {
        final Vector<Integer> v = new Vector<>(4);

        v.add(2, 100);
        System.out.println(v.size());
    }
}
