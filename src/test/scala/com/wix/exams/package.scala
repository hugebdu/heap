package com.wix

import org.specs2.matcher.Matcher

import scala.collection.GenTraversable
import scala.collection.JavaConverters._
import scala.language.implicitConversions

package object exams {

  implicit def `scala seq matcher to java iterable`[T](m: Matcher[GenTraversable[T]]): Matcher[java.lang.Iterable[T]] = {
    m.^^({ (_: java.lang.Iterable[T]).asScala })
  }
}
