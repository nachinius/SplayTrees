package com.nachinius.splay

import scala.annotation.tailrec

class Node[K,V](var key: K, var elem: V, var left: Option[Node[K,V]] = None, var right: Option[Node[K,V]] = None, var parent: Option[Node[K,V]] = None)(implicit ordered: K => Ordered[K]) extends Traversable[Node[K,V]] {
  selfNode =>
  override def toString() = s"k=$key"


  def foreach[U](f: Node[K,V] => U): Unit = {
    left.foreach(n => n.foreach(f))
    f(selfNode)
    right.foreach(n => n.foreach(f))
  }

  def depthFirst[U](f: Node[K,V] => U): Unit = {
    println("===new node")
    f(selfNode)
    left.foreach(n => {
      println("+++lll")
      n.depthFirst(f)
      println("---lll")
    })
    right.foreach(n => {
      println("+++rrr")
      n.depthFirst(f)
      println("---rrr")
    })
    println("====end node")
  }

  def isALeftChild: Boolean = parent.fold(false)(_.left.contains(selfNode))
  def isARightChild: Boolean = parent.fold(false)(_.right.contains(selfNode))
  def isALeftLeftGrandChild: Boolean = {
    parent.exists(p => p.isALeftChild && p.left.contains(selfNode))
  }
  def isARightRightGrandChild: Boolean = {
    parent.exists(p => p.isARightChild && p.right.contains(selfNode))
  }
  def setLeft(n: Option[Node[K,V]]): Unit = {
    left = n
    left.foreach(_.parent = selfNode.asOption)
  }
  def setRight(n: Option[Node[K,V]]): Unit = {
    right = n
    right.foreach(_.parent = selfNode.asOption)

  }
  def setParent(n: Option[Node[K,V]]): Unit = {
    parent = n
  }
  def asOption: Option[Node[K,V]] = Some(selfNode)


  /**
    * Obtain parent and in which branch this node is
    * @return
    */
  def childOf: (ChildKind, Node[K,V]) = {
    parent.map {
      p => if(p.left.contains(selfNode)) {
        (Left,p)
      } else {
        (Right,p)
      }
    } getOrElse (Root,selfNode)
  }

  def search(k: K): Option[Node[K,V]] = {
//    println(s"Looking on nddode with $key for key=$k")
    val comparison = key.compareTo(k)
    if (comparison > 0) left.flatMap(_.search(k))
    else if (comparison < 0) right.flatMap(_.search(k))
    else asOption
  }

  def newChild(dir: RightOrLeft)(k: K, v: V): Node[K,V] = {
    val newNode = new Node(k, v, left = None, right = None, parent = asOption)
    dir match {
      case Left =>
        left = newNode.asOption
      case Right =>
        right = newNode.asOption
    }
    newNode
  }

  /**
    * Add a key-value to the tree of this Node respecting BST property
    * @param k
    * @param v
    * @return
    */
  @tailrec
  final def add(k: K, v: V): Option[Node[K,V]] = {
    if(key < k) {
      right match {
        case None =>
          newChild(Right)(k,v).asOption
        case Some(r) => r.add(k,v)
      }
    } else if(key > k) {
      left match {
        case None =>
          newChild(Left)(k,v).asOption
        case Some(r) => r.add(k,v)
      }
    } else None
  }

  def cut(t: RightOrLeft): Option[Node[K,V]] = {
    t match {
      case Left => {
        left.foreach(_.parent = None)
        val ret = left
        left = None
        ret
      };
      case Right => {
        right.foreach(_.parent = None)
        val ret = right
        right = None
        ret
      }
    }
  }
}
