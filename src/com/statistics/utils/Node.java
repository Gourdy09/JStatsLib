package com.statistics.utils;

/**
 * <summary>
 * Node is a class representing a single node in a Red-Black Tree
 * Each node stores string data and has pointers to its parent, left child, and right child
 * 
 * Attributes:
 * - data : The value stored in the node
 * - parent : Reference to the parent node
 * - left : Reference to the left child node
 * - right : Reference to the right child node
 * - isRed : Boolean indicating the color of the node
 *           - true  = Red node
 *           - false = Black node
 * 
 * Written By:
 * - Om Patel
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
