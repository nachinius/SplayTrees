[![Build Status](https://travis-ci.org/nachinius/SplayTrees.svg?branch=master)](https://travis-ci.org/nachinius/SplayTrees)
[![Join the chat at https://gitter.im/SplayTrees/Lobby](https://badges.gitter.im/SplayTrees/Lobby.svg)](https://gitter.im/SplayTrees/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Splay Tree


### what is a Splay Tree
from https://en.wikipedia.org/wiki/Splay_tree (retrieved 2018-february-05)
>     A splay tree is a self-adjusting binary search tree with the additional property that recently accessed elements are quick to access again. It performs basic operations such as insertion, look-up and removal in O(log n) amortized time. For many sequences of non-random operations, splay trees perform better than other search trees, even when the specific pattern of the sequence is unknown. The splay tree was invented by Daniel Sleator and Robert Tarjan in 1985.
> 
>     All normal operations on a binary search tree are combined with one basic operation, called splaying. Splaying the tree for a certain element rearranges the tree so that the element is placed at the root of the tree. One way to do this is to first perform a standard binary tree search for the element in question, and then use tree rotations in a specific fashion to bring the element to the top. Alternatively, a top-down algorithm can combine the search and the tree reorganization into a single phase.

