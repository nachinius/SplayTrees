package com.nachinius.splay

import scala.annotation.tailrec

/**
  * A `Node` represents an element of tree. A `Node` without field `parent` represents a root.
  *
  * Each node may have childs. The childs are left or right.
  * The BST property is maintained by the Ordering of K.
  *
  * @param key
  * @param elem
  * @param left
  * @param right
  * @param parent
  * @param externalParent In some application of SplayTrees (like Link-cut trees), a parent pointer to an extra similar structure is needed it.
  * @param ordered
  * @tparam K types of K
  * @tparam V types of value associated with this node. It's not relevant for any internal algorithm.
  */
class Node[K,V](
                 val key: K,
                 val elem: V,
                 var left: Option[Node[K,V]] = None,
                 var right: Option[Node[K,V]] = None,
                 var parent: Option[Node[K,V]] = None,
                 var externalParent: Option[Node[K,V]] = None)
               (implicit ordered: K => Ordered[K])
  extends Traversable[Node[K,V]] {
  self =>
  override def toString() = s"Node [$key, $elem]"

  /**
    * InOrder implementation, since in splay trees, it returns the elements
    * sorted by key
    * @param f
    * @tparam U
    */
  def foreach[U](f: Node[K,V] => U): Unit = {
    left.foreach(n => n.foreach(f))
    f(self)
    right.foreach(n => n.foreach(f))
  }

  def depthFirst[U](f: Node[K,V] => U): Unit = {
    println("===new node")
    f(self)
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

  def isALeftChild: Boolean = parent.fold(false)(_.left.contains(self))
  def isARightChild: Boolean = parent.fold(false)(_.right.contains(self))
  def isALeftLeftGrandChild: Boolean = {
    parent.exists(p => p.isALeftChild && p.left.contains(self))
  }
  def isARightRightGrandChild: Boolean = {
    parent.exists(p => p.isARightChild && p.right.contains(self))
  }
  def setLeft(n: Option[Node[K,V]]): Unit = {
    left = n
    left.foreach(_.parent = self.asOption)
  }
  def setRight(n: Option[Node[K,V]]): Unit = {
    right = n
    right.foreach(_.parent = self.asOption)

  }
  def setParent(n: Option[Node[K,V]]): Unit = {
    parent = n
  }
  def asOption: Option[Node[K,V]] = Some(self)


  /**
    * Obtain parent and in which branch this node is
    * @return
    */
  def childOf: (ChildKind, Node[K,V]) = {
    parent.map {
      p => if(p.left.contains(self)) {
        (Left,p)
      } else {
        (Right,p)
      }
    } getOrElse (Root,self)
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

  @tailrec
  final def leftist: Node[K,V] =
    left match {
      case None => self
      case Some(n) => n.leftist
    }

  @tailrec
  final def rightist: Node[K,V] =
    right match {
      case None => self
      case Some(n) => n.rightist
    }
}
