package com.prizz.generic;

// Syllabus: Lectures 47-48 - Generic Class, Type Safety, and Diamond Operator usage
public class DataContainer<T> {
    private T data;
    private String label;

    public DataContainer(String label, T data) {
        this.label = label;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Container [" + label + "]: " + (data != null ? data.toString() : "empty");
    }
}
