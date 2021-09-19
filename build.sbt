enablePlugins(ScalaJSPlugin)

name := "scalajs-i18n-rx"
organization := "com.github.fbaierl"
version := "0.4.2"
scalaVersion := "2.12.15"
crossScalaVersions := List("2.12.15", "2.12.4", "2.12.3")

libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.8"
libraryDependencies += "com.timushev" %%% "scalatags-rx" % "0.4.0"
libraryDependencies += "com.lihaoyi" %%% "scalarx" % "0.4.1"
libraryDependencies += "com.github.fbaierl" %%% "scalajs-scaposer" % "0.1.2"
libraryDependencies += "org.scalactic" %%% "scalactic" % "3.0.9"
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.10" % "test"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
skip in packageJSDependencies := false