package com.nachinius.splay

import scala.annotation.tailrec



class Splay[K, V](implicit ordered: Ordering[K]) {
  self =>
  type SelfType = Splay[K,V]
  type NodeType = Node[K,V]

  var root: Option[NodeType] = None

  def search(k: K): Option[NodeType] = root.flatMap(_.search(k)).map(splay)

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

  def cut(t: RightOrLeft): SelfType = {
    val cutted = new Splay[K,V]()(ordered)
    cutted.root = root.flatMap(_.cut(t))
    cutted
  }


  def setRoot(n: Option[Node[K,V]]): Unit = {
    root = n
    n.foreach(_.setParent(None))
  }

  def handleGrandParent(x: Node[K, V], p: Node[K, V]) = {
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



