package com.wix.exams.heap;

public interface Reference {

    String id();
    int size();
    int offset();

    void refer(Reference other);
    void unrefer(Reference other);
}
