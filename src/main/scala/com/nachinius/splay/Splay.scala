package com.nachinius.splay

import scala.annotation.tailrec
import scala.collection.mutable


/**
  * A mutable Splay Tree implementation of keys K that supports satellite data of type V.
  *   A splay tree is a BST that has
  *   for certain operations
  *   - the working-set property
  *   - the dynamic-finger property
  *   - conjectured to be dynamically optimal
  *   - conjectured to have unified property
  *
  *
  *
  * @param ordering implicit ordering for K
  * @tparam K key
  * @tparam V satellite data
  */
class Splay[K, V]()(implicit ordering: Ordering[K]) extends mutable.Traversable[(K,V)] {
  self =>
  type SelfType = Splay[K,V]
  type NodeType = Node[K,V]

  var root: Option[NodeType] = None


  override def foreach[U](f: ((K, V)) => U): Unit = {
    root.foreach(
      r =>
        r.foreach((n: Node[K, V]) => f(n.key,n.elem))
    )
  }

  /**
    * Return a Node with the given key if found.
    * Splays the elements.
    *
    * @param k key to search for
    * @return
    */
  def search(k: K): Option[NodeType] = root.flatMap(_.search(k)).map(splay)

  /**
    * Add to the tree the element k, v.
    * Do not splays the element.
    *
    * @param k
    * @param v
    * @return
    */
  def add(k: K, v: V): Option[Node[K, V]] = {
    if(root.isEmpty) { root = (new Node(k,v)).asOption; root}
    else root.flatMap(_.add(k,v))
  }

  def isRoot(n: NodeType): Boolean = root.contains(n)

  @tailrec
  final def splay(n: NodeType): NodeType = {
    n.childOf match {
      case (Root,_) => n
      case (Left,p) =>
        p.childOf match {
          case (Left,gp) =>
            rotate(p)
          case (Right,gp) =>
            rotate(n)
          case (Root,_) =>
        }
        rotate(n)
        splay(n)
      case (Right,p) =>
        p.childOf match {
          case (Left,gp) =>
            rotate(p)
          case (Right,gp) =>
            rotate(n)
          case (Root,_) =>
        }
        rotate(n)
        splay(n)
    }
  }

  /**
    * Cut one of the branches from this splay tree.
    * @param t Left|Right
    * @return cutted branch as a new splay tree object.
    */
  def cut(t: RightOrLeft): SelfType = {
    val cutted = new Splay[K,V]
    cutted.root = root.flatMap(_.cut(t))
    cutted
  }

  def setRoot(n: Option[Node[K,V]]): Unit = {
    root = n
    n.foreach(_.setParent(None))
  }

  def handleGrandParent(x: Node[K, V], p: Node[K, V]): Unit = {
    p.childOf match {
      case (Root, _) =>
        setRoot(x.asOption)
      case (Left, gp) =>
        gp.setLeft(x.asOption)
      case (Right, gp) =>
        gp.setRight(x.asOption)
    }
  }

  def rotate(n: Node[K,V]): Unit = {
    n.childOf match {
      case (Root,_) => ()
      case (Left,p) =>
        handleGrandParent(n, p)
        p.setLeft(n.right)
        n.setRight(p.asOption)
      case (Right,p) =>
        handleGrandParent(n, p)
        p.setRight(n.left)
        n.setLeft(p.asOption)
    }
  }
}

object Splay {
  def apply[K, V](k: K, v: V)(implicit ordering: Ordering[K]): Splay[K, V] = {
    val res = new Splay[K, V]
    res.add(k, v)
    res
  }

  def apply[K, V](m: Map[K, V])(implicit ordering: Ordering[K]): Splay[K, V] = {
    val res = new Splay[K, V]
    m.foreach { case (k, v) => res.add(k, v) }
    res
  }

  def apply[K](ks: Seq[K])(implicit ordering: Ordering[K]): Splay[K, Any] = {
    case object Ignored
    val res = new Splay[K, Any]()
    ks.foreach(x => res.add(x, Ignored))
    res
  }
}



