package nl.han.ica.datastructures;

public class Node<T> {
    private T value;
    private Node<T> nextNode;

    Node(T value){
        this.value = value;
        this.nextNode = null;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
