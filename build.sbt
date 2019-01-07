enablePlugins(ScalaJSPlugin)

name := "scalajs-i18n-rx"
organization := "com.github.fbaierl"
version := "0.1"
scalaVersion := "2.12.6"
crossScalaVersions := List("2.12.6, 2.12.4, 2.12.3")

libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.7"
libraryDependencies += "com.timushev" %%% "scalatags-rx" % "0.3.0"
libraryDependencies += "com.lihaoyi" %%% "scalarx" % "0.3.2"
libraryDependencies += "com.github.fbaierl" %%% "scalajs-scaposer" % "0.1.2"

// publishing
homepage := Some(url("https://github.com/fbaierl/scalajs-i18n-rx"))
licenses += ("MIT License", url("http://www.opensource.org/licenses/mit-license.php"))
scmInfo := Some(ScmInfo(
  url("https://github.com/fbaierl/scalajs-i18n-rx"),
  "scm:git:git@github.com/fbaierl/scalajs-i18n-rx.git",
  Some("scm:git:git@github.com/fbaierl/scalajs-i18n-rx.git")))
publishMavenStyle := true
isSnapshot := false
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
pomExtra :=
  <developers>
    <developer>
      <id>fbaierl</id>
      <name>Florian Baierl</name>
      <url>https://github.com/fbaierl</url>
    </developer>
  </developers>
pomIncludeRepository := { _ => false }