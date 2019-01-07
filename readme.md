# scalajs-i18n-rx

*Change the language of your entire web app with one line of code*:

```
I18n.changeLanguage(Locale.de)
````

**scalajs-i18n-rx** is a small internationalization library for Scala.js that combines 
[scalatags](https://github.com/lihaoyi/scalatags), 
[scala.rx](https://github.com/lihaoyi/scala.rx), 
[scalatags-rx](https://github.com/rtimush/scalatags-rx) and
[scalajs-scaposer](https://github.com/fbaierl/scalajs-scaposer)
 to provide HTML DOM elements that automatically reload whenever the language to display is changed.

With **scalajs-i18n-rx** one can:

- Load and combine PO files ([The format of PO files](https://www.gnu.org/software/gettext/manual/html_node/PO-Files.html))
- Create automatically updating, localized dom elements
- Change the language of an entire web app with one line of code during runtime

## API


```$scala
tr(singular: String): Rx.Dynamic[String])
tr(context: String, singular: String): Rx.Dynamic[String]
```

## Basic Usage

```$scala
// First of all some necessary imports
import scalatags.JsDom.all._
import scalatags.rx.all._
import com.github.fbaierl.i18nrx._
import rx.Ctx.Owner.Unsafe._

```
````$scala
// a minimal example of a PO file
val dePoFile = """
msgid "Hello World"
msgstr "Hallo Welt"
"""

// load po files
I18n.loadPoFile(Locale.de, dePoFile)
I18n.loadPoFile(Locale.de_CH, chPoFile)
// 

// dom creation
div(p(I18n.tr("Hello World")))
// ...

// change language
I18n.changeLanguage(Locale.de)
// now all previously created dom elements show the German translation
````

Easily add your own custom Locales: 

```
val standardJapanese = """
msgid "Really?"
msgstr "本当？"
"""
val kansaiJapanese = """
msgid "Really?"
msgstr "ほんま？"
"""

I18n.loadPoFile(Locale.jp, standardJapanese)
I18n.loadPoFile(Locale("Japanese (Kansai)","jp_ka"), kansaiJapanese
```

## Installation

build.sbt example:
TODO NOT RELEASED YET
```
libraryDependencies += "com.github.fbaierl" %%% "scalajs-i18n-rx" % "0.1"
```