package com.nachinius.splay


import org.scalacheck.Prop
import org.scalacheck.Prop.{forAll, BooleanOperators}
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Arbitrary.arbitrary

import scala.util.Random


object NodeProperties extends Properties("SplayNode") {
  import Prop.BooleanOperators
  type NodeType = Node[Int,NotUsed]

  case class KeyAndTree(lst: List[Int], node: NodeType)

  val genKeyAndSplayTree: Gen[KeyAndTree] = for {
    head <- arbitrary[Int]
    ls <- arbitrary[List[Int]]
  } yield KeyAndTree(head :: ls, ls.foldLeft(Node(head)) {
    case (splay: Node[Int, NotUsed], i: Int) =>
      splay.insert(i,NotUsed)
  })

  val genNode: Gen[NodeType] = for {
    t <- genKeyAndSplayTree
  } yield t.node

  implicit val agkt = Arbitrary(genKeyAndSplayTree)
  implicit val agn = Arbitrary(genNode)

  def random = new Random(324769007324L)

  property("All inserted key are found if searched and they become root") = forAll {
    KeyAndTree: KeyAndTree =>
      random.shuffle(KeyAndTree.lst).forall(k =>
        KeyAndTree.node.search(k).exists(_.isRoot))
  }

  property("binary search property") = forAll {
    (node: NodeType) => {
      node.forall {
        n =>
          n.left.forall(l => l.key < n.key) &&
          n.right.forall(r => r.key > n.key)
      }
    }
  }

  property("The sum of the cutted trees is equal to the original size") = forAll {
    (node: NodeType) => {
      val lengthOriginal: Int = node.toSeq.length
      val cuttedLength = node.split.map(_.toSeq.length).getOrElse(0)
      lengthOriginal == (cuttedLength + node.toSeq.length)
    }
  }

  property("If tree has a parentPointer the splay node always have it") = forAll {
    (kt: KeyAndTree, exnode: NodeType) =>
      // set the external parent
      kt.node.externalParent = exnode.asOption
      random.shuffle(kt.lst).forall(k =>
        kt.node.search(k).exists(_.externalParent.exists(_ == exnode))
      )
  }

  property("All keys are present in the traversal") = forAll {
    kt: KeyAndTree =>
      kt.node.map(_.key).toList.sorted == kt.lst.sorted.distinct
  }

  property("leftist get the minimum of all keys") = forAll {
    kt: KeyAndTree =>
      kt.node.map(_.key).min == kt.node.leftist.key
  }

  property("rightist get the max of all keys") = forAll {
    kt: KeyAndTree =>
      kt.node.map(_.key).max == kt.node.rightist.key
  }

  property("two element inserted into the same tree are connected") = forAll {
    n: NodeType => {
      (n.size > 1) ==> {
        val lst = n.toList

        def check(head: NodeType, rest: Seq[NodeType]): Boolean =
          rest.forall(r => head.isConnected(r))

        val tuplesOfChecks = lst.tail.scanLeft((lst.head, lst.tail))({
          case (acc: (Node[Int, NotUsed], List[Node[Int, NotUsed]]), n: Node[Int, NotUsed]) => (n, acc._2.tail)
        })
        tuplesOfChecks.forall((check _).tupled)
      }
    }
  }

  property("elements of different trees aren't connect") = forAll {
    (n: NodeType, n2: NodeType) =>
      n2.forall(
        n2node => !n.isConnected(n2node)
      )
  }

}
