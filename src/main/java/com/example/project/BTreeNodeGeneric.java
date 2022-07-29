package com.example.project;

import java.util.Vector;

public class BTreeNodeGeneric<E extends Comparable<? super E>> {
    Vector<E> keys; // keys of nodes
    int minDeg; // Minimum degree of B-tree node
    Vector<BTreeNodeGeneric<E>> children;  // Child node
    int num; // Number of keys of node
    boolean isLeaf; // True when leaf node

    public static <E> void initializeVectorAsArray(Vector<E> v, int size) {
        for(int i = 0; i < size; i++)
            v.add(null);
    }

    // Constructor
    public BTreeNodeGeneric(int minDeg, boolean isLeaf) {
        this.minDeg = minDeg;
        this.isLeaf = isLeaf;
        
        this.keys = new Vector<E>(2*this.minDeg-1); // Node has 2*minDeg-1 keys at most
        BTreeNodeGeneric.initializeVectorAsArray(this.keys, 2*this.minDeg-1);
        this.children = new Vector<BTreeNodeGeneric<E>>(2*this.minDeg);
        BTreeNodeGeneric.initializeVectorAsArray(this.children, 2*this.minDeg);
        
        this.num = 0;
    }

    public void insertNotFull(E key){
        int i = this.num -1; // Initialize i as the rightmost index

        if(this.isLeaf){ // When it is a leaf node
            // Find the location where the new key should be inserted
            while (i >= 0 && this.keys.get(i).compareTo(key) > 0){
                this.keys.set(i+1, this.keys.get(i)); // keys backward shift
                i--;
            }
            this.keys.set(i+1, key);
            this.num = this.num + 1;
        }
        else{
            // Find the child node location that should be inserted
            while (i >= 0 && this.keys.get(i).compareTo(key) > 0)
                i--;
            if (children.get(i+1).num == 2*this.minDeg - 1){ // When the child node is full
                splitChild(i+1, this.children.get(i+1));
                // After splitting, the key in the middle of the child node moves up, and the child node splits into two
                if (this.keys.get(i+1).compareTo(key) < 0)
                    i++;
            }
            this.children.get(i+1).insertNotFull(key);
        }
    }


    public void splitChild(int i, BTreeNodeGeneric<E> y){

        // First, create a node to hold the keys of MinDeg-1 of y
        BTreeNodeGeneric<E> z = new BTreeNodeGeneric<>(y.minDeg, y.isLeaf);
        z.num = this.minDeg - 1;

        // Pass the properties of y to z
        for (int j = 0; j < this.minDeg-1; j++)
            z.keys.set(j, y.keys.get(j+this.minDeg));
        if (!y.isLeaf){
            for (int j = 0; j < this.minDeg; j++)
                z.children.set(j, y.children.get(j+this.minDeg));
        }
        y.num = this.minDeg-1;

        // Insert a new child into the child
        for (int j = num; j >= i+1; j--)
            this.children.set(j+1, children.get(j));
        this.children.set(i+1, z);

        // Move a key in y to this node
        for (int j = num-1;j >= i;j--)
            this.keys.set(j+1, keys.get(j));
        this.keys.set(i, y.keys.get(this.minDeg-1));

        this.num = this.num + 1;
    }

    public void traverse(){
        int i;
        for (i = 0; i < this.num; i++){
            if (!isLeaf)
                this.children.get(i).traverse();
            System.out.printf(" %d", this.keys.get(i));
        }

        if (!isLeaf){
            this.children.get(i).traverse();
        }
    }

    public boolean search(E key){
        int i = 0;
        while (i < this.num && key.compareTo(this.keys.get(i)) > 0)
            i++;

        if (this.keys.get(i) != null && this.keys.get(i).compareTo(key) == 0)
            return true;
        if (this.isLeaf)
            return false;
        return this.children.get(i).search(key);
    }
}
