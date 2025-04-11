package com.statistics.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Collections;

/**
 * <summary>
 * RBTree provides an implementation for storing data
 * 
 * Usage:
 * - Create a RBTree object and store data within it
 * 
 * </summary>
 */
 
public class RBTree {
    private Node root;
    private final Node NULLNODE;
    
    private int size;

    /**
     * Constructs a new RBTree instance
     */
    public RBTree() {
        NULLNODE = new Node(null);
        NULLNODE.isRed = false;
        root = NULLNODE;
    }
    
    public Node getRoot() { return root; }

    // Insertions
    public void insert(Double key) {
        Node node = root;
        Node parent = null;

        while (node != NULLNODE) {
            parent = node;
            int cmp = key.compareTo(node.data);

            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                // Put duplicates on the left
                node = node.left;
            }
        }

        Node newNode = new Node(key);
        newNode.left = NULLNODE;
        newNode.right = NULLNODE;
        newNode.isRed = true;

        if (parent == null) {
            root = newNode;
        } else {
            int cmp = key.compareTo(parent.data);
            if (cmp <= 0) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }
        }

        newNode.parent = parent;
        fixTree(newNode);
        size++;
    }

    // Fix issues in tree
    private void fixTree(Node node) {
        while (node != root && node.parent.isRed) {
            Node parent = node.parent;
            Node grandparent = parent.parent;

            if (grandparent == null) {
                parent.isRed = false;
                return;
            }

            Node uncle = getUncle(parent);

            // Case 1: Uncle is red
            if (uncle != null && uncle.isRed) {
                parent.isRed = false;
                uncle.isRed = false;
                grandparent.isRed = true;
                node = grandparent;
            }
            // Case 2 and 3: Uncle is black or null
            else {
                if (parent == grandparent.left) {
                    if (node == parent.right) {
                        rotateLeft(parent);
                        node = parent;
                        parent = node.parent;
                    }
                    rotateRight(grandparent);
                } else {
                    if (node == parent.left) {
                        rotateRight(parent);
                        node = parent;
                        parent = node.parent;
                    }
                    rotateLeft(grandparent);
                }
                parent.isRed = false;
                grandparent.isRed = true;
                break;
            }
        }
        root.isRed = false;
    }

    // Rotations
    private void rotateLeft(Node node) {
        if (node.right == NULLNODE) {
            return;
        }
        Node right = node.right;
        node.right = right.left;

        if (right.left != NULLNODE) {
            right.left.parent = node;
        }

        right.parent = node.parent;

        if (node.parent == null) {
            root = right;
        } else if (node == node.parent.left) {
            node.parent.left = right;
        } else {
            node.parent.right = right;
        }

        right.left = node;
        node.parent = right;
    }

    private void rotateRight(Node node) {
        if (node.left == NULLNODE) {
            return;
        }
        Node left = node.left;
        node.left = left.right;

        if (left.right != NULLNODE) {
            left.right.parent = node;
        }

        left.parent = node.parent;

        if (node.parent == null) {
            root = left;
        } else if (node == node.parent.right) {
            node.parent.right = left;
        } else {
            node.parent.left = left;
        }

        left.right = node;
        node.parent = left;
    }

    // Refrences
    private Node getUncle(Node parent) {
        Node grandparent = parent.parent;
        if (grandparent == null) {
            return null;
        }

        if (grandparent.left == parent) {
            return grandparent.right;
        } else {
            return grandparent.left;
        }
    }

    /**
     * Convert Red Black Tree to frequency map
     * @return Hashmap with entries representing <number, frequency>
     */
    public HashMap<Double, Integer> toFrequencyMap() {
        HashMap<Double, Integer> freqMap = new HashMap<>();
        populateFrequencyMap(freqMap);
        return freqMap;
    }

    /**
     * Populate frequency map with entries
     * @param freqMap the frequency map to populate
     */
    private void populateFrequencyMap(HashMap<Double, Integer> freqMap) {
        Stack<Node> stack = new Stack<>();
        Node current = root;

        while (current != NULLNODE || !stack.isEmpty()) {
            while (current != NULLNODE) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            if (current.data != null) {
                freqMap.put(current.data, freqMap.getOrDefault(current.data, 0) + 1);
            }
            current = current.right;
        }
    }
    
    /**
     * Convert Red Black Tree to an ArrayList
     * @return ArrayList representation of Red Black Tree
     */
    public ArrayList<Double> toArrayList() {
        ArrayList<Double> result = new ArrayList<>();
        Deque<Node> stack = new LinkedList<>();
        Node current = root;

        while (current != NULLNODE || !stack.isEmpty()) {
            while (current != NULLNODE) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.data);
            current = current.right;
        }
        
        Collections.sort(result);
        return result;
    }

    // Check if node exists in tree
    public boolean contains(Double key) {
        Node node = root;
        while (node != NULLNODE) {
            int cmp = key.compareTo(node.data);
            if (cmp == 0) {
                return true;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return false;
    }

    // Height
    public int getHeight() {
        return getHeight(root);
    }
    private int getHeight(Node node) {
        if (node == NULLNODE) {
            return 0;
        }
        int leftHeight = getHeight(node.left);
        int rightHeight = getHeight(node.right);
        return Math.max(leftHeight, rightHeight) + 1;
    }
    
    // Size
    public int getSize(){
      return size;
    }
}
