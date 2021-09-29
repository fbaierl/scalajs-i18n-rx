enablePlugins(ScalaJSPlugin)

name := "scalajs-i18n-rx"
organization := "com.github.fbaierl"
version := "0.4.2"
scalaVersion := "2.13.6"
crossScalaVersions := List("2.12.15", "2.12.4", "2.12.3")

resolvers += "Artima Maven Repository" at "https://repo.artima.com/releases"

libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.9.4"
libraryDependencies += "com.timushev" %%% "scalatags-rx" % "0.5.0"
libraryDependencies += "com.lihaoyi" %%% "scalarx" % "0.4.3"
libraryDependencies += "com.github.fbaierl" %%% "scalajs-scaposer" % "1.11.1"
libraryDependencies += "org.scalactic" %%% "scalactic" % "3.2.10"
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.10" % "test"