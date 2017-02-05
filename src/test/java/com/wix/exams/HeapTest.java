package com.wix.exams;

import org.junit.Test;

import static com.jcabi.matchers.RegexMatchers.matchesPattern;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class HeapTest {

    @Test
    public void freeRemovesReference() throws Exception {
        Heap heap = Heap.empty();

        Heap.Reference root1 = heap.allocateRoot(10);
        Heap.Reference reference = heap.allocate(25, root1);

        heap.free(reference);

        assertThat(heap.allReferences(), not(contains(reference)));
        assertThat(root1.references(), not(contains(reference)));
    }

    @Test(expected = Heap.OutOfMemoryException.class)
    public void allocationFailsOnOutOfSpace() throws Exception {
        Heap heap = Heap.empty(10);

        heap.allocate(11);
    }

    @Test
    public void referenceHasReferences() throws Exception {
        Heap heap = Heap.empty();

        Heap.Reference root1 = heap.allocateRoot(10);
        Heap.Reference reference = heap.allocate(25, root1);

        assertThat(root1.references(), contains(reference));
    }

    @Test
    public void rootReferenceListedInAll() throws Exception {
        Heap heap = Heap.empty();
        Heap.Reference root = heap.allocateRoot(10);

        assertThat(heap.allReferences(), contains(root));
    }

    @Test
    public void rootReferenceListedInRoots() throws Exception {
        Heap heap = Heap.empty();
        Heap.Reference root = heap.allocateRoot(10);

        assertThat(heap.rootReferences(), contains(root));
    }

    @Test
    public void nonRootReferenceListedInAll() throws Exception {
        Heap heap = Heap.empty();

        Heap.Reference reference = heap.allocate(25);

        assertThat(heap.allReferences(), contains(reference));
    }

    @Test
    public void nonRootReferenceNotListedInRoots() throws Exception {
        Heap heap = Heap.empty();

        Heap.Reference reference = heap.allocate(25);

        assertThat(heap.rootReferences(), not(contains(reference)));
    }

    @Test
    public void referenceHasSize() throws Exception {
        Heap heap = Heap.empty();
        Heap.Reference root = heap.allocateRoot(10);

        assertThat(root.size(), equalTo(10));
    }

    @Test
    public void referenceToString() throws Exception {
        Heap heap = Heap.empty();
        Heap.Reference root = heap.allocateRoot(10);

        assertThat(root.toString(), matchesPattern("Reference.*size.*"));
    }
}
