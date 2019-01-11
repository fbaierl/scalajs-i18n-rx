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