package com.nachinius.splay

import org.scalatest.{FlatSpec, Matchers}

class NodeTest extends FlatSpec with Matchers {

  behavior of "NodeTest"

  def construct = {
    val root = new Node(1,1)
    val left = new Node(2,2)
    val right = new Node(3,3)
    root.setLeft(left.asOption)
    root.setRight(right.asOption)
    val leftleft = new Node(4,4)
    val leftright = new Node(5,5)
    left.setLeft(leftleft.asOption)
    left.setRight(leftright.asOption)

    (root,left,right, leftleft, leftright)
  }

  it should "cut" in {
    val (a,b,c,d,e) = construct

    val cutted = a.cut(Right)
    cutted shouldBe Some(c)
    a.right shouldBe None
    cutted.flatMap(_.parent) shouldBe None

    val cutleft = a.cut(Left)
    cutleft shouldBe Some(b)
    a.left shouldBe None
    cutleft.flatMap(_.left) shouldBe Some(d)
    cutleft.flatMap(_.right) shouldBe Some(e)
  }

  it should "search" in {
    val (a,b,c,d,e) = construct

    a.search(6) shouldBe None
    a.search(3) shouldBe Some(c)
  }

  it should "add" in {
    val a = new Node(1,1)
    val b = a.add(4,4).get
    val c = a.add(8,8).get
    val d = a.add(6,6).get
    val e = a.add(3,3).get
    val z = a.add(0,0).get
    val w = a.add(7,7).get
    val no = a.add(7,7)
    no shouldBe None

    w.childOf shouldBe (Right,d)
    z.childOf shouldBe (Left,a)
    e.childOf shouldBe (Left,b)
    d.childOf shouldBe (Left,c)
    c.childOf shouldBe (Right,b)
    b.childOf shouldBe (Right,a)
  }

  it should "childOf" in {
    val (root,left,right,d,e) = construct

    left.childOf shouldBe (Left,root)
    right.childOf shouldBe (Right,root)
    root.childOf shouldBe (Root,root)
    d.childOf shouldBe (Left,left)
    e.childOf shouldBe (Right,left)
  }


}
