package com.nachinius

package object splay {

  sealed trait ChildKind
  sealed trait RightOrLeft extends ChildKind
  case object Right extends RightOrLeft {
    val inverse: RightOrLeft = Left
  }
  case object Left extends RightOrLeft {
    val inverse: RightOrLeft = Right
  }
  case object Root extends ChildKind

  sealed trait NotUsed
  case object NotUsed extends NotUsed
}
