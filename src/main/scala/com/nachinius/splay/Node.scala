package com.nachinius.splay

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

/**
  * A mutable Splay Tree implementation of keys K that supports satellite data of type V.
  *
  * A splay tree is a BST that has
  * for certain operations
  *   - the working-set property
  *   - the dynamic-finger property
  *   - conjectured to be dynamically optimal
  *   - conjectured to have unified property
  *
  * A `Node` represents an element of tree. A `Node` without field `parent` represents a root.
  *
  * Each node may have children. The children are left or right.
  * The BST property is maintained by the Ordering of K.
  * See properties in [NodeProperties]
  *
  * @param key            Used for Ordering, and they must be unique in the tree.
  * @param elem
  * @param left
  * @param right
  * @param parent
  * @param externalParent In some application of SplayTrees (like Link-cut trees), a parent pointer to an extra similar structure is needed it.
  * @param ordered
  * @tparam K types of K
  * @tparam V types of value associated with this node. It's not relevant for any internal algorithm.
  */
class Node[K, V](
                  var key: K,
                  var elem: V,
                  var left: Option[Node[K, V]] = None,
                  var right: Option[Node[K, V]] = None,
                  var parent: Option[Node[K, V]] = None,
                  var externalParent: Option[Node[K, V]] = None)
                (implicit ordered: K => Ordered[K])
  extends scala.collection.mutable.Traversable[Node[K, V]] {
  self =>


  override def toString() = s"Node [$key, $elem]"

  /**
    * InOrder implementation, since in splay trees, it returns the elements
    * sorted by key
    *
    * @param f
    * @tparam U
    */
  def foreach[U](f: Node[K, V] => U): Unit = {
    left.foreach(n => n.foreach(f))
    f(self)
    right.foreach(n => n.foreach(f))
  }

  def print(identation: Int = 0): Seq[String] = {
    val sol = ArrayBuffer[String]()
    val tabs = " " * identation
    val length = identation + key.toString.length
    left.foreach({ ele =>
      sol += " " * length + "L{{"
      sol ++= ele.print(length + 6)
      sol += " " * length + "L}}"
    })
    sol += (tabs + "("+key.toString+")")
    right.foreach({ ele =>
      sol += " " * length + "[[R"
      sol ++= ele.print(length + 6)
      sol += " " * length + "R]]"
    })
    sol
  }

  def isRoot: Boolean = parent.isEmpty

  def search(k: K): Option[Node[K, V]] = {
    splay // we can't search correctly from somewhere which is not the root
    searchDown(k).map(found => {
      found.splay
      found
    })
  }

  /**
    * Insert a key-value into the splay tree.
    * It splays the result, and replacement the V element if key already exists.
    *
    * @param k
    * @param v
    * @return
    */
  def insert(k: K, v: V): Node[K, V] = {
    splay // we make sure self is root
    add(k, v).getOrElse {
      self.elem = v
      self
    }.splay // the inserted element is splayed and returned

  }

  @tailrec
  final def leftist: Node[K, V] =
    left match {
      case None => self
      case Some(n) => n.leftist
    }

  @tailrec
  final def rightist: Node[K, V] =
    right match {
      case None => self
      case Some(n) => n.rightist
    }

  def min: K = leftist.key
  def max: K = rightist.key

  @tailrec
  final def findRoot: Node[K,V] = parent match {
    case None => self
    case Some(p) => p.findRoot
  }
  def isConnected(that: Node[K,V]): Boolean = {
    self.splay
    that.findRoot == self
  }
  /**
    * Brings the node up, until it becomes root.
    *
    * @return
    */
  def splay: Node[K, V] = {
    self.childOf match {
      case (Root, _) => () // "I Am Root"
      case (a, p) =>
        p.childOf match {
          case (Root, _) =>
          case (b, _) =>
            if (a == b) p.rotate
            else self.rotate
        }
        rotate
        splay // keep splaying yourself until become root
    }
    self
  }

  def split: Option[Node[K, V]] = {
    splay
    cut(Right)
  }
  def splitLeft: Option[Node[K,V]] = {
    splay
    cut(Left)
  }

  def setLeft(n: Option[Node[K, V]]): Unit = {
    left = n
    left.foreach(_.parent = self.asOption)
  }

  def setRight(n: Option[Node[K, V]]): Unit = {
    right = n
    right.foreach(_.parent = self.asOption)

  }

  def asOption: Option[Node[K, V]] = Some(self)

  /**
    * Obtain parent and in which branch this node is
    *
    * @return
    */
  private[splay] def childOf: (ChildKind, Node[K, V]) = {
    parent.map {
      p =>
        if (p.left.contains(self)) {
          (Left, p)
        } else {
          (Right, p)
        }
    } getOrElse(Root, self)
  }

  private[splay] def newChild(dir: RightOrLeft)(k: K, v: V): Node[K, V] = {
    val newNode = new Node(k, v, left = None, right = None, parent = asOption)
    dir match {
      case Left =>
        setLeft(newNode.asOption)
      case Right =>
        setRight(newNode.asOption)
    }
    newNode
  }

  /**
    * Add a key-value to the tree of this Node respecting BST property
    *
    * @param k
    * @param v
    * @return
    */
  @tailrec
  private[splay] final def add(k: K, v: V): Option[Node[K, V]] = {
    if (key < k) {
      right match {
        case None =>
          newChild(Right)(k, v).asOption
        case Some(r) => r.add(k, v)
      }
    } else if (key > k) {
      left match {
        case None =>
          newChild(Left)(k, v).asOption
        case Some(r) => r.add(k, v)
      }
    } else None
  }

  private[splay] def cut(t: RightOrLeft): Option[Node[K, V]] = {
    t match {
      case Left =>
        left.foreach(_.parent = None)
        val ret = left
        left = None
        ret
      case Right =>
        right.foreach(_.parent = None)
        val ret = right
        right = None
        ret
    }
  }

  private[splay] def rotate(): Unit = self.childOf match {
    case (Root, _) => () // "I am Root" "I am Root" :)
    case (Left, p) =>
      handleGrandParent(p)
      p.setLeft(self.right)
      self.setRight(p.asOption)
    case (Right, p) =>
      handleGrandParent(p)
      p.setRight(self.left)
      self.setLeft(p.asOption)
  }

  private[splay] def handleGrandParent(parentNode: Node[K, V]): Unit = {
    parentNode.parent.fold {
      // parent will stop to be the root
      self.externalParent = parentNode.externalParent
      parentNode.externalParent = None
      self.parent = None
      // No other Root bookkeeping
    } {
      gp => gp.replaceChild(parentNode, self)
    }
  }

  /**
    * Replace the passed child for another. Figures it out automatically in which branch. Updates all pointers.
    *
    * @param oldChild
    * @param newChild
    * @return True if there were changes, false otherwise.
    */
  private[splay] def replaceChild(oldChild: Node[K, V], newChild: Node[K, V]): Unit = {
    if (left.contains(oldChild)) {
      left = newChild.asOption
      newChild.parent = self.asOption
    } else if (right.contains(oldChild)) {
      right = newChild.asOption
      newChild.parent = self.asOption
    }
  }

  /**
    * Search for a key in this node and its children.
    *
    * @param k key to search
    * @return If found, the Node to which the key belongs.
    */
  private[splay] def searchDown(k: K): Option[Node[K, V]] = {
    //    println(s"Looking on nddode with $key for key=$k")
    val comparison = key.compareTo(k)
    if (comparison > 0) left.flatMap(_.searchDown(k))
    else if (comparison < 0) right.flatMap(_.searchDown(k))
    else asOption
  }

  def globalUpdateKey(f: K => K) = {
    splay
    foreach( n => n.key = f(n.key))
  }

}

object Node {

  def apply(k: Int): Node[Int, NotUsed] = new Node[Int, NotUsed](k, NotUsed)

}

