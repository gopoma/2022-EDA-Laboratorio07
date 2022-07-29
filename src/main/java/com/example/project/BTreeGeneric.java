package com.example.project;

public class BTreeGeneric<E extends Comparable<? super E>> {
    BTreeNodeGeneric<E> root;
    int minDeg;

    // Constructor
    public BTreeGeneric(int minDeg){
        this.root = null;
        this.minDeg = minDeg;
    }

    public void traverse(){
        if (this.root != null){
            this.root.traverse();
        } else {
            System.out.println("The BTree is Empty!");
        }
    }

    public void add(E value) {
        if (this.root == null){
            this.root = new BTreeNodeGeneric<E>(this.minDeg,true);
            this.root.keys.set(0, value);
            this.root.num = 1;
        }
        else {
            // When the root node is full, the tree will grow high
            if(this.root.num == 2*this.minDeg-1){
                BTreeNodeGeneric<E> s = new BTreeNodeGeneric<>(this.minDeg,false);
                // The old root node becomes a child of the new root node
                s.children.set(0, this.root);
                // Separate the old root node and give a key to the new node
                s.splitChild(0, this.root);
                // The new root node has 2 child nodes. Move the old one over there
                int i = 0;
                if(s.keys.get(0).compareTo(value) < 0)
                    i++;
                s.children.get(i).insertNotFull(value);

                this.root = s;
            }
            else
                this.root.insertNotFull(value);
        }
    }

    public E remove(E value) {
        //TODO implement here!
        return null;
    }

    public void clear() {
        this.root = null;
    }

    public boolean search(E value) {
        //TODO implement here!
        return false;
    }

    public int size() {
        //TODO implement here!
        return 0;
    }

    public static void main(final String[] args) {
        BTreeGeneric<Integer> t = new BTreeGeneric<>(2); // A B-Tree with minium degree 2
        t.add(1);
        t.add(3);
        t.add(7);
        t.add(10);
        t.add(11);
        t.add(13);
        t.add(14);
        t.add(15);
        t.add(18);
        t.add(16);
        t.add(19);
        t.add(24);
        t.add(25);
        t.add(26);
        t.add(21);
        t.add(4);
        t.add(5);
        t.add(20);
        t.add(22);
        t.add(2);
        t.add(17);
        t.add(12);
        t.add(6);

        System.out.println("Traversal of tree constructed is");
        t.traverse();
        System.out.println();

        t.clear();
        t.traverse();
    }
}
