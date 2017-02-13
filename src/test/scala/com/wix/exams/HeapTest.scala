package com.wix.exams

import com.wix.exaps.heap.ReferenceDSL._
import com.wix.exaps.heap.{Heap, Reference}
import org.specs2.matcher.{Expectable, MatchResult, Matcher}
import org.specs2.mutable.SpecificationWithJUnit

import scala.collection.GenTraversable

class HeapTest extends SpecificationWithJUnit {

  "Heap.allocate" should {

    "generate unique ids" >> {
      val heap = Heap.empty(20)

      val objects = for (_ <- 0 until 20) yield heap.allocate(1)

      objects must haveUniqueIds
    }

    "allocate objects without overlapping" >> {
      val heap = Heap.empty(10)

      heap.allocateRoot(1)
      heap.allocate(2)
      heap.allocate(3)
      heap.allocate(4)

      heap.references() must notOverlap
    }

    "fail for object bigger than the heap capacity" >> {
      val heap = Heap.empty(10)

      heap.allocateRoot(11) must throwA[Heap.OutOfMemory]
    }

    "fail to allocate when no space left on the heap" >> {
      val heap = Heap.empty(10)

      heap.allocateRoot(5)
      heap.allocate(6) must throwA[Heap.OutOfMemory]
    }

    "succeed after garbage collection" >> {
      val heap = Heap.empty(10)

      heap.allocate(10)
      heap.gc()
      heap.allocate(9)

      success
    }

    "fail to allocate due to fragmentation" >> {
      val heap = Heap.empty(10)

      val root = heap.allocateRoot(2)
      val ref1 = heap.allocate(2)
      val ref2 = heap.allocate(2)
      val ref3 = heap.allocate(2)
      val ref4 = heap.allocate(2)
      root ---> ref2 ---> ref4

      heap.gc(false)

      heap.allocate(3) must throwA[Heap.OutOfMemory]
    }

    "succeed after garbage collection with compaction" >> {
      val heap = Heap.empty(10)

      val root = heap.allocateRoot(2)
      val ref1 = heap.allocate(2)
      val ref2 = heap.allocate(2)
      val ref3 = heap.allocate(2)
      val ref4 = heap.allocate(2)
      root ---> ref2 ---> ref4

      heap.gc(true)

      heap.allocate(3)
      success
    }
  }

  "Heap.gc" should {

    "not collect roots" >> {
      val heap = Heap.empty()

      val root1 = heap.allocateRoot()
      val root2 = heap.allocateRoot()

      heap.gc()

      heap.references must contain(root1, root2)
    }

    "not collect referenced object" >> {
      val heap = Heap.empty()
      val root = heap.allocateRoot()
      val ref1 = heap.allocate()
      val ref2 = heap.allocate()
      root ---> ref1 ---> ref2

      heap.gc()

      heap.references must contain(ref1, ref2)
    }

    "collect unreferenced object" >> {
      val heap = Heap.empty()
      val ref = heap.allocate()

      heap.gc()

      heap.references must not(contain(ref))
    }

    "collect unreferenced objects cluster" >> {
      val heap = Heap.empty()
      val ref1 = heap.allocate()
      val ref2 = heap.allocate()
      val ref3 = heap.allocate()
      ref1 ---> ref2 ---> ref3 ---> ref1

      heap.gc()

      heap.references must not(contain(ref1, ref2, ref3))
    }

    "collect unreferenced object that reference to a live object" >> {
      val heap = Heap.empty()
      val root = heap.allocateRoot()
      val ref1 = heap.allocate()
      val ref2 = heap.allocate()
      root ---> ref1
      ref2 ---> ref1

      heap.gc()

      heap.references must not(contain(ref2))
    }

    "be repetitive" >> {
      val heap = Heap.empty()
      val root = heap.allocateRoot()
      val ref1 = heap.allocate()
      val ref2 = heap.allocate()
      root ---> ref1 ---> ref2

      heap.gc()

      ref1 -/-> ref2

      heap.gc()

      heap.references must not(contain(ref2))
    }
  }

  def haveUniqueIds: Matcher[GenTraversable[Reference]] = new Matcher[GenTraversable[Reference]] {
    override def apply[S <: GenTraversable[Reference]](t: Expectable[S]): MatchResult[S] = {
      val refs = t.value.toList
      val nonUniqueIds = refs.groupBy(_.id()) collect {
        case (id, instances) if instances.size > 1 => id
      }

      result(nonUniqueIds.isEmpty, "references have unique ids", s"non-unique reference ids found: [${nonUniqueIds.toList.mkString(", ")}]", t)
    }
  }

  def notOverlap: Matcher[GenTraversable[Reference]] = new Matcher[GenTraversable[Reference]] {
    override def apply[S <: GenTraversable[Reference]](t: Expectable[S]): MatchResult[S] = {
      val references = t.value.toIndexedSeq.sortBy(_.offset)
      val overlaps = references.sliding(2) collectFirst {
        case Seq(r1, r2) if r1.offset() + r1.size() > r2.offset() => (r1, r2)
      }

      overlaps.fold(success("no overlapping references found", t)) {
        case (r1, r2) => failure(s"overlapping references found: $r1 with $r2", t)
      }
    }         
  }
}
