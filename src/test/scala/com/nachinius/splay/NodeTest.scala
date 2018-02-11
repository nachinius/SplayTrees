package com.nachinius.splay

import org.scalacheck.Test.TestCallback
import org.scalatest.FreeSpec

class NodeTest extends FreeSpec {

  import org.scalacheck.Test
  import Test._

  "Node should check all laws" in {
    val prms = Test.Parameters.defaultVerbose
    val ps = NodeProperties
    val params = ps.overrideParameters(prms)
    ps.properties.map { case (name,p) =>
      val testCallback = new TestCallback {
        override def onPropEval(n: String, t: Int, s: Int, d: Int) =
          params.testCallback.onPropEval(name,t,s,d)
        override def onTestResult(n: String, r: Result) =
          params.testCallback.onTestResult(name,r)
      }
      val res = check(params.withTestCallback(testCallback), p)
      (name,res)
    }.foreach {
      case (name: String, result: Result) =>
        assert(result.passed,name)
    }
  }

}
