package nl.han.ica.datastructures;

public class HANLinkedList<T> implements IHANLinkedList<T> {

    private Node<T> headNode = null;
    private int size = 0;

    @Override
    public void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.setNextNode(headNode);
        headNode = newNode;
        size++;
    }

    @Override
    public void clear() {
        headNode = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (index == 0 ){
            addFirst(value);
        } else {

            Node<T> currentNode = headNode;
            for (int i = 0; i < index; i++) {
                currentNode = currentNode.getNextNode();
            }

            Node<T> newNode = new Node<>(value);
            newNode.setNextNode(currentNode.getNextNode());
            currentNode.setNextNode(newNode);
            size++;
        }
    }

    @Override
    public void delete(int pos) {
        if (pos == 0 ){
            removeFirst();;
        } else {
            Node<T> previousNode = headNode;
            for (int i = 0; i < pos - 1; i++) {
                previousNode = previousNode.getNextNode();
            }

            previousNode.setNextNode(previousNode.getNextNode().getNextNode());
            size--;
        }
    }

    @Override
    public T get(int pos) {
        Node<T> currentNode = headNode;
        for (int i = 0; i < pos; i++) {
            currentNode = currentNode.getNextNode();
        }
        return  currentNode.getValue();
    }

    @Override
    public void removeFirst() {
        headNode = headNode.getNextNode();
        size--;
    }

    @Override
    public T getFirst() {
        return headNode.getValue();
    }

    @Override
    public int getSize() {
        return size;
    }
}
