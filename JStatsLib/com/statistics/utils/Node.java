package com.statistics.utils;

/**
 * <summary>
 * Node is a class representing a single node in a Red-Black Tree
 * Each node stores string data and has pointers to its parent, left child, and right child
 * </summary>
 */
public class Node {
    Double data;
    Node parent;
    Node left;
    Node right;
    boolean isRed;

    Node(Double data) {
        this.data = data;
        this.parent = null;
        this.left = null;
        this.right = null;
        this.isRed = true;
    }
    
    public Double toDouble(){
      return data;
    }
}
