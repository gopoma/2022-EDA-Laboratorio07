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

    public void remove(E value) {
        if (this.root == null){
            System.out.println("The tree is empty");
            return;
        }

        this.root.remove(value);

        if (this.root.num == 0){ // If the root node has 0 keys
            // If it has a child, its first child is taken as the new root,
            // Otherwise, set the root node to null
            if (this.root.isLeaf)
                this.root = null;
            else
                this.root = this.root.children.get(0);
        }
    }

    public void clear() {
        this.root = null;
    }

    // Function to find value
    public boolean search(E value) {
        return this.root == null ? false : this.root.search(value);
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

        System.out.printf("Contains 15?: %b%n", t.search(15));
        System.out.printf("Contains 17?: %b%n", t.search(17));
        System.out.printf("Contains 56?: %b%n", t.search(56));
        System.out.printf("Contains 12?: %b%n", t.search(12));
        System.out.printf("Contains 2?: %b%n", t.search(2));
        System.out.printf("Contains 100?: %b%n", t.search(100));
        System.out.printf("Contains 18?: %b%n", t.search(18));

        t.remove(6);
        System.out.println("Traversal of tree after removing 6");
        t.traverse();
        System.out.println();

        t.remove(13);
        System.out.println("Traversal of tree after removing 13");
        t.traverse();
        System.out.println();

        t.remove(7);
        System.out.println("Traversal of tree after removing 7");
        t.traverse();
        System.out.println();

        t.remove(4);
        System.out.println("Traversal of tree after removing 4");
        t.traverse();
        System.out.println();

        t.remove(2);
        System.out.println("Traversal of tree after removing 2");
        t.traverse();
        System.out.println();

        t.remove(16);
        System.out.println("Traversal of tree after removing 16");
        t.traverse();
        System.out.println();

        t.clear();
        t.traverse();
    }
}
