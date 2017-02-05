package com.wix.exams;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Heap {

    public static final int DEFAULT_MAX_SIZE = 100;

    private final Set<Reference> roots = new HashSet<>();
    private final Set<Reference> nonRoots = new HashSet<>();
    private final int maxSize;

    private Heap(int maxSize) {
        this.maxSize = maxSize;
    }

    public static Heap empty() {
        return empty(DEFAULT_MAX_SIZE);
    }

    public static Heap empty(int maxSize) {
        return new Heap(maxSize);
    }

    public Reference allocateRoot(int size) {
        verifyHasSpace(size);
        Reference reference = new Reference(size);
        roots.add(reference);
        return reference;
    }

    public Reference allocate(int size, Reference ... parents) {
        verifyHasSpace(size);
        Reference reference = new Reference(size);
        nonRoots.add(reference);
        Stream.of(parents).forEach(p -> p.addReferenceTo(reference));
        return reference;
    }

    private void verifyHasSpace(int size) {
        int occupied = allReferences().stream().mapToInt(Reference::size).sum();
        if (occupied + size > maxSize)
            throw new OutOfMemoryException();
    }

    public void gc() {
        //TODO: implement mark & sweep
    }

    public ImmutableSet<Reference> allReferences() {
        return ImmutableSet.<Reference>builder()
                .addAll(roots)
                .addAll(nonRoots).build();
    }

    public ImmutableSet<Reference> rootReferences() {
        return ImmutableSet.copyOf(roots);
    }

    public void free(Reference reference) {
        nonRoots.remove(reference);
        allReferences().forEach(r -> r.removeReferenceTo(reference));
    }


    class Reference {

        private final int size;
        private final Set<Reference> references = new HashSet<>();

        private Reference(int size) {
            this.size = size;
        }

        public int size() {
            return size;
        }

        @Override
        public String toString() {
            return "Reference{" +
                    "size=" + size +
                    '}';
        }

        public ImmutableSet<Reference> references() {
            return ImmutableSet.copyOf(references);
        }

        public void addReferenceTo(Reference reference) {
            references.add(reference);
        }

        public void removeReferenceTo(Reference reference) {
            references.remove(reference);
        }
    }

    public static class OutOfMemoryException extends RuntimeException{
    }
}
