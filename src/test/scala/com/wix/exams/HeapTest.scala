package com.wix.exams

import com.wix.exams.heap.Heap
import com.wix.exams.heap.ReferenceDSL._
import org.specs2.mutable.SpecificationWithJUnit

class HeapTest extends SpecificationWithJUnit with ReferenceMatchers {

  "object allocation" should {

    "store allocated objects on the heap" >> {
      implicit val heap = Heap.empty()

      val obj1 = new RootObject()
      val obj2 = new Object()

      heap.references must contain(obj1, obj2)
    }

    "allocate objects without overlapping" >> {
      implicit val heap = Heap.empty(10)

      new RootObject(1)
      new Object(2)
      new Object(3)
      new Object(4)

      heap.references must notOverlap
    }

    "fail for object bigger than the heap capacity" >> {
      implicit val heap = Heap.empty(10)

      new RootObject(11) must throwA[Heap.OutOfMemory]
    }

    "fail to allocate when no space left on the heap" >> {
      implicit val heap = Heap.empty(10)

      new RootObject(5)
      new Object(6) must throwA[Heap.OutOfMemory]
    }

    "succeed after garbage collection" >> {
      implicit val heap = Heap.empty(10)

      new Object(10)
      heap.gc(false)
      new Object(9)

      success
    }

    "fail to allocate due to fragmentation" >> {
      implicit val heap = Heap.empty(10)

      val root = new RootObject(2)
      val ref1 = new Object(2)
      val ref2 = new Object(2)
      val ref3 = new Object(2)
      val ref4 = new Object(2)
      root ---> ref2 ---> ref4

      heap.gc(false)

      new Object(3) must throwA[Heap.OutOfMemory]
    }

    "succeed after garbage collection with compaction" >> {
      implicit val heap = Heap.empty(10)

      val root = new RootObject(2)
      val ref1 = new Object(2)
      val ref2 = new Object(2)
      val ref3 = new Object(2)
      val ref4 = new Object(2)
      root ---> ref2 ---> ref4

      heap.gc(true)

      new Object(3)
      success
    }
  }

  "garbage collection" should {

    "not collect roots" >> {
      implicit val heap = Heap.empty()

      val root1 = new RootObject()
      val root2 = new RootObject()

      heap.gc()

      heap.references must contain(root1, root2)
    }

    "not collect referenced object" >> {
      implicit val heap = Heap.empty()
      val root = new RootObject()
      val ref1 = new Object()
      val ref2 = new Object()
      root ---> ref1 ---> ref2

      heap.gc()

      heap.references must contain(ref1, ref2)
    }

    "collect unreferenced object" >> {
      implicit val heap = Heap.empty()
      val ref = new Object()

      heap.gc()

      heap.references must not(contain(ref))
    }

    "collect unreferenced objects cluster" >> {
      implicit val heap = Heap.empty()
      val ref1 = new Object()
      val ref2 = new Object()
      val ref3 = new Object()
      ref1 ---> ref2 ---> ref3 ---> ref1

      heap.gc()

      heap.references must not(contain(ref1, ref2, ref3))
    }

    "collect unreferenced object that reference to a live object" >> {
      implicit val heap = Heap.empty()
      val root = new RootObject()
      val ref1 = new Object()
      val ref2 = new Object()
      root ---> ref1
      ref2 ---> ref1

      heap.gc()

      heap.references must not(contain(ref2))
    }

    "be repetitive" >> {
      implicit val heap = Heap.empty()
      val root = new RootObject()
      val ref1 = new Object()
      val ref2 = new Object()
      root ---> ref1 ---> ref2

      heap.gc()

      ref1 -/-> ref2

      heap.gc()

      heap.references must not(contain(ref2))
    }
  }
}
