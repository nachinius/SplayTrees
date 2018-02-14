lazy val root = project.in(file("."))
val username = "nachinius"

name := "SplayTrees"
organization := "com.nachinius"
description := "A SplayTree implementation"
version := "0.1"
val repo = "SplayTrees"

scalaVersion := "2.12.4"
val scalaTestVersion = "3.0.4"
//libraryDependencies += "org.scalactic" %% "scalactic" % scalaTestVersion
libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % "test"

val scalaCheckVersion = "1.13.4"
libraryDependencies += "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test"

homepage := Some(url(s"https://github.com/$username/$repo"))
licenses += "Apache License 2.0" -> url(s"https://github.com/$username/$repo/blob/master/LICENSE")
scmInfo := Some(ScmInfo(url(s"https://github.com/$username/$repo"), s"git@github.com:$username/$repo.git"))
//  apiURL := Some(url(s"https://$username.github.io/$repo/latest/api/")),
releaseCrossBuild := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value
developers := List(
  Developer(id = username, name = "Ignacio `nachinius` Peixoto", email = "ignacio.peixoto@gmail.com", url = new URL(s"http://github.com/${username}"))
)

publishMavenStyle := true
publishArtifact in Test := false


publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)

