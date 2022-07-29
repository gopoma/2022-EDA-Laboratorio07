package com.example.project;

import java.util.Vector;

public class VectorAplication {
    public static void main(String[] args) {
        final Vector<Integer> v = new Vector<>(4);
        v.add(0);
        v.add(1);
        v.add(2);
        v.add(3);

        // System.out.println(v.get(2));

        for(int i = 0; i < v.size(); i++) {
            System.out.println(v.get(i));
        }

        v.remove(2);

        for(int i = 0; i < v.size(); i++) {
            System.out.println(v.get(i));
        }

        v.add(1, 100);
        System.out.println(v.get(1));

        for(int i = 0; i < v.size(); i++) {
            System.out.println(v.get(i));
        }
    }
}
