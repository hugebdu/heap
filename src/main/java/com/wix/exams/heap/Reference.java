package com.wix.exams.heap;

public interface Reference {

    int size();
    int offset();

    void refer(Reference other);
    void unrefer(Reference other);
}
