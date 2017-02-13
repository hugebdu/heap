package com.wix.exaps.heap

trait ReferenceDSL {

  implicit class ReferenceWithOperators(ref: Reference) {

    def --->(other: Reference): Reference = { ref.reference(other); other }

    def -/->(other: Reference): Unit = ref.dereference(other)
  }
}

object ReferenceDSL extends ReferenceDSL
