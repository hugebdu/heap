package com.wix.exams.heap;

public interface Reference {

    String id();
    int size();
    int offset();

    void reference(Reference other);
    void dereference(Reference other);
}