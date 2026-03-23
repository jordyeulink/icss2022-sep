package nl.han.ica.datastructures;

import java.util.ArrayList;

public class HanStack<T> implements IHANStack<T>{

    private ArrayList<T> stackItems;

    public HanStack() {
        stackItems = new ArrayList<>();
    }

    @Override
    public void push(T value) {
        stackItems.add(value);
    }

    @Override
    public T pop() {
        if (stackItems.isEmpty()) {
            throw new RuntimeException("Stack is empty!");
        }
        return stackItems.remove(stackItems.size() - 1);
    }

    @Override
    public T peek() {
        if (stackItems.isEmpty()) {
            throw new RuntimeException("Stack is empty!");
        }
        return stackItems.get(stackItems.size() - 1);
    }
}