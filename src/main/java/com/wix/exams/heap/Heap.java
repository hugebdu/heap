package com.wix.exams.heap;

import java.util.Set;

public final class Heap {

    private static final int DEFAULT_HEAP_CAPACITY = 50;

    public static Heap empty(int capacity) {
        //TODO: implement
        return null;
    }

    public static Heap empty() {
        return empty(DEFAULT_HEAP_CAPACITY);
    }

    public Reference allocateRoot(int size) {
        //TODO: implement
        return null;
    }

    public void gc(boolean compact) {
        //TODO: implement
    }


    public void gc() {
        gc(true);
    }

    public Reference allocate(int size) {
        //TODO: implement
        return null;
    }

    public Set<Reference> references() {
        //TODO: implement
        return null;
    }

    public static final class OutOfMemory extends RuntimeException {

        OutOfMemory() {
            super("No more space left in the heap");
        }
    }
}
