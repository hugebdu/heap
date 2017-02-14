package com.wix.exams

import com.wix.exams.heap.Reference
import org.specs2.matcher.Matchers._
import org.specs2.matcher.{Expectable, MatchResult, Matcher, TraversableMatchers}

import scala.collection.GenTraversable

trait ReferenceMatchers {

  def contain[T <% Reference](t: T*): Matcher[GenTraversable[Reference]] = {
    val asReferences = t.map(t => t: Reference)
    TraversableMatchers.contain(TraversableMatchers.allOf(asReferences: _*))
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

object ReferenceMatchers extends ReferenceMatchers
