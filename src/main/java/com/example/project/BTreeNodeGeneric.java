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
        int i = this.num - 1; // Initialize i as the rightmost index

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
            if (this.children.get(i+1).num == 2*this.minDeg-1){ // When the child node is full
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
        for (int j = this.num-1;j >= i;j--)
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
        while (i < this.num && (this.keys.get(i) == null || key.compareTo(this.keys.get(i)) > 0))
            i++;

        if (this.keys.get(i) != null && this.keys.get(i).compareTo(key) == 0)
            return true;
        if (this.isLeaf)
            return false;
        return this.children.get(i).search(key);
    }

    // Here Starts the Hardest part of the Implementation

    // Find the first location index equal to or greater than key
    public int findKey(E key) {
        int idx = 0;
        // The conditions for exiting the loop are: 1.idx == num, i.e. scan all of them once
        // 2. IDX < num, i.e. key found or greater than key
        while (idx < this.num && this.keys.get(idx).compareTo(key) < 0)
            ++idx;
        return idx;
    }


    public void remove(E key){

        int idx = findKey(key);
        if (idx < this.num && this.keys.get(idx).compareTo(key) == 0){ // Find key
            if (this.isLeaf) // key in leaf node
                removeFromLeaf(idx);
            else // key is not in the leaf node
                removeFromNonLeaf(idx);
        }
        else{
            if (this.isLeaf){ // If the node is a leaf node, then the node is not in the B tree
                System.out.printf("The key %d is does not exist in the tree\n",key);
                return;
            }

            // Otherwise, the key to be deleted exists in the subtree with the node as the root

            // This flag indicates whether the key exists in the subtree whose root is the last child of the node
            // When idx is equal to num, the whole node is compared, and flag is true
            boolean flag = idx == this.num; 
            
            if (children.get(idx).num < this.minDeg) // When the child node of the node is not full, fill it first
                fill(idx);


            //If the last child node has been merged, it must have been merged with the previous child node, so we recurse on the (idx-1) child node.
            // Otherwise, we recurse to the (idx) child node, which now has at least the keys of the minimum degree
            if (flag && idx > num)
                this.children.get(idx-1).remove(key);
            else
                this.children.get(idx).remove(key);
        }
    }

    public void removeFromLeaf(int idx){

        // Shift from idx
        for (int i = idx +1;i < num;++i)
            this.keys.set(i-1, this.keys.get(i));
        num --;
    }

    public void removeFromNonLeaf(int idx){

        E key = this.keys.get(idx);

        // If the subtree before key (children[idx]) has at least t keys
        // Then find the precursor 'pred' of key in the subtree with children[idx] as the root
        // Replace key with 'pred', recursively delete pred in children[idx]
        if(this.children.get(idx).num >= this.minDeg){
            E pred = getPred(idx);
            this.keys.set(idx, pred);
            this.children.get(idx).remove(pred);
        }
        // If children[idx] has fewer keys than MinDeg, check children[idx+1]
        // If children[idx+1] has at least MinDeg keys, in the subtree whose root is children[idx+1]
        // Find the key's successor 'succ' and recursively delete succ in children[idx+1]
        else if(this.children.get(idx+1).num >= this.minDeg){
            E succ = getSucc(idx);
            this.keys.set(idx, succ);
            this.children.get(idx+1).remove(succ);
        }
        else{
            // If the number of keys of children[idx] and children[idx+1] is less than MinDeg
            // Then key and children[idx+1] are combined into children[idx]
            // Now children[idx] contains the 2t-1 key
            // Release children[idx+1], recursively delete the key in children[idx]
            merge(idx);
            this.children.get(idx).remove(key);
        }
    }

    public E getPred(int idx){ // The predecessor node is the node that always finds the rightmost node from the left subtree

        // Move to the rightmost node until you reach the leaf node
        BTreeNodeGeneric<E> cur = this.children.get(idx);
        while (!cur.isLeaf)
            cur = cur.children.get(cur.num);
        return cur.keys.get(cur.num-1);
    }

    public E getSucc(int idx){ // Subsequent nodes are found from the right subtree all the way to the left

        // Continue to move the leftmost node from children[idx+1] until it reaches the leaf node
        BTreeNodeGeneric<E> cur = this.children.get(idx+1);
        while (!cur.isLeaf)
            cur = cur.children.get(0);
        return cur.keys.get(0);
    }

    // Fill children[idx] with less than MinDeg keys
    public void fill(int idx){

        // If the previous child node has multiple MinDeg-1 keys, borrow from them
        if (idx != 0 && this.children.get(idx-1).num >= this.minDeg)
            borrowFromPrev(idx);
        // The latter sub node has multiple MinDeg-1 keys, from which to borrow
        else if (idx != this.num && this.children.get(idx+1).num >= this.minDeg)
            borrowFromNext(idx);
        else{
            // Merge children[idx] and its brothers
            // If children[idx] is the last child node
            // Then merge it with the previous child node or merge it with its next sibling
            if (idx != this.num)
                merge(idx);
            else
                merge(idx-1);
        }
    }

    // Borrow a key from children[idx-1] and insert it into children[idx]
    public void borrowFromPrev(int idx){

        BTreeNodeGeneric<E> child = this.children.get(idx);
        BTreeNodeGeneric<E> sibling = this.children.get(idx-1);

        // The last key from children[idx-1] overflows to the parent node
        // The key[idx-1] underflow from the parent node is inserted as the first key in children[idx]
        // Therefore, sibling decreases by one and children increases by one
        for (int i = child.num-1; i >= 0; --i) // children[idx] move forward
            child.keys.set(i+1, child.keys.get(i));

        if (!child.isLeaf){ // Move children[idx] forward when they are not leaf nodes
            for (int i = child.num; i >= 0; --i)
                child.children.set(i+1, child.children.get(i));
        }

        // Set the first key of the child node to the keys of the current node [idx-1]
        child.keys.set(0, this.keys.get(idx-1));
        if (!child.isLeaf) // Take the last child of sibling as the first child of children[idx]
            child.children.set(0, sibling.children.get(sibling.num));

        // Move the last key of sibling up to the last key of the current node
        keys.set(idx-1, sibling.keys.get(sibling.num-1));
        child.num += 1;
        sibling.num -= 1;
    }

    // Symmetric with borowfromprev
    public void borrowFromNext(int idx){

        BTreeNodeGeneric<E> child = this.children.get(idx);
        BTreeNodeGeneric<E> sibling = this.children.get(idx+1);

        child.keys.set(child.num, keys.get(idx));

        if (!child.isLeaf)
            child.children.set(child.num+1, sibling.children.get(0));

        this.keys.set(idx, sibling.keys.get(0));

        for (int i = 1; i < sibling.num; ++i)
            sibling.keys.set(i-1, sibling.keys.get(i));

        if (!sibling.isLeaf){
            for (int i= 1; i <= sibling.num;++i)
                sibling.children.set(i-1, sibling.children.get(i));
        }
        child.num += 1;
        sibling.num -= 1;
    }

    // Merge childre[idx+1] into childre[idx]
    public void merge(int idx){

        BTreeNodeGeneric<E> child = this.children.get(idx);
        BTreeNodeGeneric<E> sibling = this.children.get(idx+1);

        // Insert the last key of the current node into the MinDeg-1 position of the child node
        child.keys.set(this.minDeg-1, this.keys.get(idx));

        // keys: children[idx+1] copy to children[idx]
        for (int i=0 ; i< sibling.num; ++i)
            child.keys.set(i+this.minDeg, sibling.keys.get(i));

        // children: children[idx+1] copy to children[idx]
        if (!child.isLeaf){
            for (int i = 0;i <= sibling.num; ++i)
                child.children.set(i+this.minDeg, sibling.children.get(i));
        }

        // Move keys forward, not gap caused by moving keys[idx] to children[idx]
        for (int i = idx+1; i<num; ++i)
            this.keys.set(i-1, this.keys.get(i));
        // Move the corresponding child node forward
        for (int i = idx+2;i<=num;++i)
            this.children.set(i-1, children.get(i));

        child.num += sibling.num + 1;
        num--;
    }

    public int size(){
        int size = 0;
        int i;
        for (i = 0; i < this.num; i++){
            if (!isLeaf)
                size += this.children.get(i).size();
            size++;
        }

        if (!isLeaf)
            size += this.children.get(i).size();
        return size;
    }
}
