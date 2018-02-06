package com.nachinius

package object splay {

  sealed trait ChildKind
  sealed trait RightOrLeft extends ChildKind
  case object Right extends RightOrLeft {
    val inverse = Left
  }
  case object Left extends RightOrLeft {
    val inverse = Right
  }
  case object Root extends ChildKind

}
