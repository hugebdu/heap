package com.wix.exams.heap

import scala.language.implicitConversions

trait ReferenceDSL {

  class RootObject(size: Int = Heap.DEFAULT_OBJECT_SIZE)(implicit heap: Heap) extends Object(size) {

    override protected def allocate(size: Int): Reference = {
      heap.allocateRoot(size)
    }
  }

  class Object(size: Int = Heap.DEFAULT_OBJECT_SIZE)(implicit heap: Heap) {

    val reference = allocate(size)

    protected def allocate(size: Int): Reference = heap.allocate(size)

    override def toString: String = reference.toString

    override def equals(obj: scala.Any): Boolean = reference.equals(obj)

    override def hashCode(): Int = reference.hashCode()
  }

  implicit def objectToReference(obj: Object): Reference = obj.reference

  implicit class ReferenceWithOperators[T <% Reference](ref: T) {

    def --->(other: Reference): Reference = { ref.refer(other); other }

    def -/->(other: Reference): Unit = ref.unrefer(other)
  }
}

object ReferenceDSL extends ReferenceDSL
