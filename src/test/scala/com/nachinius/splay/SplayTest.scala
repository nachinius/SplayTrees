package com.nachinius.splay

import org.scalatest.{FreeSpec, Matchers, OptionValues}
import Matchers._

import scala.util.Random

class SplayTest extends FreeSpec with Matchers with OptionValues {

  implicit val ord =  new scala.math.Ordering.IntOrdering {}
//  import scala.math.Ordering.Implicits._
  def intStringSplay: Splay[Int, String] = new Splay[Int, String]

  /**
            5
        3       7
      1  4    6   8
    0  2
   */
  def example1: Splay[Int,String] = {
    val s = intStringSplay
    s.add(5, "5")
    s.add(7, "7")
    s.add(6,"6")
    val three = s.add(3, "3")
    s.add(1, "1")
    s.add(4, "4")
    s.add(0, "0")
    s.add(2,"2")
    s.add(8,"8")
    s
  }

  "A Splay Tree" - {
    "rotate" - {
      "when root should do nothing" in {
        val s = example1
        val v = s.root.value.toVector
        val one = v(7)
        s.rotate(one)
        s.isRoot(one) shouldBe true
        s.rotate(one)
        s.isRoot(one) shouldBe true
      }
      "when left child of root should set new root" in {
        val s = example1
        val v = s.root.value.toVector
        val x = v(3)
        s.rotate(x)
        s.isRoot(x) shouldBe true
      }
      "when right child of root should set new root" in {
        val s = example1
        val v = s.root.value.toVector
        val x = v(7)
        s.rotate(x)
        s.isRoot(x) shouldBe true
      }
      "when left child should work" in {
        val s = example1
        val v = s.root.value.toVector
        val x = v(0)
        s.rotate(x)
        x.childOf shouldBe (Left,v(3))
        v(1).childOf shouldBe (Right,v(0))
      }
      "when right child should work" in {
        val s = example1
        val v = s.root.value.toVector
        val x = v(8)
        s.rotate(x)
        x.childOf shouldBe (Right,v(5))
      }
    }
    "search" - {
      "should work when empty" in {
        val a = intStringSplay
        a.search(2) shouldBe None
      }
    }
    "add" - {
      "should add elements" in {
        val splay = intStringSplay
        val b = splay.add(2, "2").value
        val c = splay.add(3, "3").value

        b.key shouldBe 2
        b.elem shouldBe "2"

        c.key shouldBe 3
        c.elem shouldBe "3"
      }
      "when key already exists" - {
        "should not add the element" in {
          val splay = intStringSplay
          val b = splay.add(2,"2")
          val c = splay.add(3,"3")

          val X = splay.add(2,"22")
          X shouldBe None
        }
      }
    }




    "splay" - {
      "should put splayed element in top" in {
        val splay = intStringSplay
        val seed = 54745L
        val rnd = new Random(seed)
        val nmbrs = (1 to 10 by 3).toList
        val nodeAdded: List[Node[Int, String]] = rnd.shuffle(nmbrs).flatMap(i => splay.add(i, i.toString))

        rnd.shuffle(nodeAdded).forall({
          n =>
            splay.splay(n)
            splay.isRoot(n)
        }) shouldBe true
      }
    }
    "search" - {
      "should put searched element in top" in {
        val splay = intStringSplay
        val seed = 54745L
        val rnd = new Random(seed)
        val nmbrs: List[Int] = (1 to 100 by 3).toList
        val added = rnd.shuffle(nmbrs).flatMap(i => splay.add(i, i.toString))
        val shuffled = rnd.shuffle(nmbrs)
        shuffled.forall { i =>
          val searched = splay.search(i)
          searched.nonEmpty shouldBe true
          val result = splay.isRoot(searched.value)
          result
        }
      }
    }

    "cut" - {
      "should split the tree in two parts" in {
        val splay = intStringSplay
        val seed = 54543L
        val rnd = new Random(seed)
        val elements = 100
        val added = List.fill(elements)(rnd.nextInt()).map(i => splay.add(i, i.toString()))
        val left = splay.root.flatMap(_.left)
        val right: Option[Node[Int, String]] = splay.root.flatMap(_.right)

        val cutted = splay.cut(Right)
        cutted.root shouldBe right
        splay.root.flatMap(_.right) shouldBe None

        cutted.root.exists(_.parent.isEmpty) shouldBe true
        splay.root.exists(_.parent.isEmpty) shouldBe true
        splay.root.flatMap(_.left) shouldBe left
      }

    }
  }
}
