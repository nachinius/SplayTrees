[![Build Status](https://travis-ci.org/nachinius/SplayTrees.svg?branch=master)](https://travis-ci.org/nachinius/SplayTrees)
[![codecov](https://codecov.io/gh/nachinius/SplayTrees/branch/master/graph/badge.svg)](https://codecov.io/gh/nachinius/SplayTrees)
[![Coverage Status](https://coveralls.io/repos/github/nachinius/SplayTrees/badge.svg?branch=master)](https://coveralls.io/github/nachinius/SplayTrees?branch=master)
[![Join the chat at https://gitter.im/SplayTrees/Lobby](https://badges.gitter.im/SplayTrees/Lobby.svg)](https://gitter.im/SplayTrees/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Latest version](https://index.scala-lang.org/nachinius/splaytrees/splaytrees/latest.svg)](https://index.scala-lang.org/nachinius/splaytrees/splaytrees)


# Splay Tree
A `splay-tree` is a BST, self adjusting, that has

- the working-set property
- the dynamic-finger property
- conjectured to be dynamically optimal
- conjectured to have unified property

## install with sbt
```sbt
    val splayTreesVersion = "0.1.1"
    libraryDependencies += "com.nachinius" %% "splaytrees" % splayTreesVersion
```

## usage

```scala
import com.nachinius.splay.Node

val objTres = new Node[Int,String](3,"tres")
val objCinco = objTres.insert(5,"cinco")

// better to insert in last inserted node (which is the root). Nevertheless you may insert anywhere, but if you use a node that is not already the root, the system will invoke an extra `splay` call

val objSeven = objCinco.insert(7, "siete")
// equivalent (in result, not performance) to
// val objSeven = objTress.insert(7,"siete")

//----
// To add a sequence of tuples (key: Key, value: Value)
val seq: (Key, Value)
seq.foldLeft(objTres) {
  case (n, (k,v)) => n.insert(k,v)
}
// e.g.
val seed = 1475648325L
val rnd = new scala.util.Random(seed)
val maxNumber = 10000
val last = Seq.fill(100)(rnd.nextInt(maxNumber)).foldLeft(objTres) {
 case (n, (k,v)) => n.insert(k, v.toString)
}
//--- 

//And then you can search
last.search(5)

// split at ndoe
last.split

// or split at a found value (we have inserted 7 before)
val optCuttedTree = last.search(7).map(_.split)

// you can call splay directly in any node, (if you have athe node reference)
last.splay

```
    
    
    

### what is a Splay Tree

#### References
[1] Advanced Data Structures, MIT 6.851, Prof. Erik Demaine Lecture 19

[2] https://en.wikipedia.org/wiki/Splay_tree (retrieved 2018-february-05):





