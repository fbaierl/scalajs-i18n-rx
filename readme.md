[![Build Status](https://travis-ci.org/fbaierl/scalajs-i18n-rx.svg?branch=master)](https://travis-ci.org/fbaierl/scalajs-i18n-rx) 
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg)](https://www.scala-js.org)
# scalajs-i18n-rx

*Change the language of your entire web app with one line of code*:

```
I18n changeLanguage Locale.de
```

**scalajs-i18n-rx** is a small internationalization library for Scala.js that combines 
[scalatags](https://github.com/lihaoyi/scalatags), 
[scala.rx](https://github.com/lihaoyi/scala.rx), 
[scalatags-rx](https://github.com/rtimush/scalatags-rx) and
[scalajs-scaposer](https://github.com/fbaierl/scalajs-scaposer)
 to provide HTML DOM elements that automatically reload whenever the language to display is changed.

With **scalajs-i18n-rx** one can:

- Load and combine PO files ([The format of PO files](https://www.gnu.org/software/gettext/manual/html_node/PO-Files.html))
- Create automatically updating, localized DOM elements
- Change the language of an entire web app with one line of code during runtime

## Basic Usage

```scala
// First of all some necessary imports
import scalatags.JsDom.all._
import scalatags.rx.all._
import com.github.fbaierl.i18nrx.I18n._
import rx.Ctx.Owner.Unsafe._

// a minimal example of a PO file
val dePoFile = """
msgid "Hello World"
msgstr "Hallo Welt"
"""

// load po files
I18n.loadPoFile(Locale.de, dePoFile)

// dom creation
div(p(tx("Hello World")))
// ...

// change language
I18n.changeLanguage(Locale.de)
// now all previously created dom elements show the German translation
```

#### Custom Locales: 

```scala
val standardJapanese = """
msgid "Really?"
msgstr "本当？"
"""

val kansaiJapanese = """
msgid "Really?"
msgstr "ほんま？"
"""

I18n.loadPoFile(Locale.ja, standardJapanese)
I18n.loadPoFile(Locale("Japanese (Kansai)","ja_ka"), kansaiJapanese)
```

#### Plurals 

Working with plurals works best if the number deciding which plural form will be used (`n`) itself is an `Rx`.
It can be used directly like this:

```scala
import scalatags.JsDom.all._
import scalatags.rx.all._
import com.github.fbaierl.i18nrx.I18n._
import rx.Ctx.Owner.Unsafe._

val frPO =
  """
    |msgid ""
    |msgstr "Plural-Forms: nplurals=2; plural=n>1;"
    |
    |msgid "I have one apple"
    |msgid_plural "I have %1$s apples"
    |msgstr[0] "J'ai une pomme"
    |msgstr[1] "J'ai %1$s pommes"
  """.stripMargin

val dePO =
  """
    |msgid ""
    |msgstr "Plural-Forms: nplurals=2; plural=n>1;"
    |
    |msgid "I have one apple"
    |msgid_plural "I have %1$s apples"
    |msgstr[0] "Ich habe einen Apfel"
    |msgstr[1] "Ich habe %1$s Äpfel"
  """.stripMargin
  
I18n.loadPoFile(Locale.fr, frPO)
I18n.loadPoFile(Locale.de, dePO)
I18n.changeLanguage(Locale.fr)
    
val amountOfApples = Var(1.toLong)
val element = p(
  Rx {
    val form = tnx("I have one apple", "I have %1$s apples", amountOfApples)
    String.format(form(), amountOfApples().toString) }
).render

println(element.innerHTML) // "J'ai une pomme"

amountOfApples() = 2
println(element.innerHTML) // "J'ai 2 pommes"

I18n.changeLanguage(Locale.en)
println(element.innerHTML) // "I have 2 apples"

amountOfApples() = 3
I18n.changeLanguage(Locale.de)
println(element.innerHTML) // "Ich habe 3 Äpfel"
```

### Combining PO files

Multiple PO files of the same Locale can be combined:

```scala

val japaneseSpiderPo =
  """
    |# an oriental species of golden orb-weaving spider
    |msgid "nephila clavata"
    |msgstr "女郎蜘蛛"
  """.stripMargin
  
val japanesePO =
  """
    |msgid "I like %1$s."
    |msgstr "%1$sが好き。"
  """.stripMargin
  
I18n.loadPoFile(Locale.ja, japanesePO)
I18n.loadPoFile(Locale.ja, japaneseSpiderPo)
I18n.changeLanguage(Locale.ja)
val sentence = String.format(t("I like %1$s."), t("nephila clavata"))
println(sentence) // "女郎蜘蛛が好き。"
```

## API

```scala
  /**
    * Changes the language to display.
    * @param locale the language to display
    */
  def changeLanguage(locale: Locale): Unit

  /**
    * @return a set of all languages available
    */
  def availableLanguages: Set[Locale]

  /**
    * @return the currently active language
    */
  def activeLanguage: Locale

  /**
    * Loads a PO file. Adds the given language to the dictionary.
    * If a PO file with the same [[com.github.fbaierl.i18nrx.Locale]] was loaded before, the language files are merged
    * together
    * @param locale the locale of the PO file
    * @param fileContent content of the PO file
    */
  @throws(classOf[PoFileParseException])
  def loadPoFile(locale: Locale, fileContent: String): Unit

  /**
    * The default language to display.
    */
  def defaultLanguage: Locale

  /**
    * Translates a singular.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tx(...).apply()` so that the
    *       value gets updated automatically.
    * @param singular the text to translate
    * @return a reactive wrapping a translatable singular text
    */
  def tx(singular: String)(implicit ctx: Ctx.Owner): Rx.Dynamic[String]

  /**
    * Translates a singular.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tcx(...).apply()` so that the
    *       value gets updated automatically.
    * @param context the context of the text to translate
    * @param singular the text to translate
    * @return a reactive wrapping a translatable singular text determined by a context
    */
  def tcx(context: String, singular: String)(implicit ctx: Ctx.Owner): Rx.Dynamic[String]

  /**
    * Translates a plural.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tnx(...).apply()` so that the
    *       value gets updated automatically.
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return a reactive wrapping a translatable plural text
    */
  def tnx(singular: String, plural: String, n: Long)(implicit ctx: Ctx.Owner): Rx.Dynamic[String] 

  /**
    * Translates a plural. Automatically updates the DOM element if n is updated.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tnx(...).apply()`
    *       so that the value gets updated automatically.
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural (a Rx)
    * @return a reactive wrapping a translatable plural text
    */
  def tnx(singular: String, plural: String, n: Rx[Long])(implicit ctx: Ctx.Owner): Rx.Dynamic[String] 

  /**
    * Translates a plural with context.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tcnx(...).apply()` so that
    *       the value gets updated automatically.
    * @param context the context of the text to translate
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return a reactive wrapping a translatable plural text determined by a context
    */
  def tcnx(context: String, singular: String, plural: String, n: Long)(implicit ctx: Ctx.Owner) : Rx.Dynamic[String] 

  /**
    * Translates a plural with context. Automatically updates the DOM element if n is updated.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tcnx(...).apply()` so that the
    *       value gets updated automatically.
    * @param context the context of the text to translate
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural (a Rx)
    * @return a reactive wrapping a translatable plural text determined by a context
    */
  def tcnx(context: String, singular: String, plural: String, n: Rx[Long])(implicit ctx: Ctx.Owner) : Rx.Dynamic[String]

  /**
    * Translates a singular.
    * @param singular the text to translate
    * @return the translated singular
    */
  def t(singular: String): String

  /**
    * Translates a singular with context.
    * @param context the context of the text to translate
    * @param singular the text to translate
    * @return the translated singular
    */
  def tc(context: String, singular: String): String

  /**
    * Translates a plural.
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return the translated plural
    */
  def tn(singular: String, plural: String, n: Long): String

  /**
    * Translates a plural with context.
    * @param context the context of the text to translate
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return the translated plural
    */
  def tcn(context: String, singular: String, plural: String, n: Long): String
```

## Installation

build.sbt example:

```scala
libraryDependencies += "com.github.fbaierl" %%% "scalajs-i18n-rx" % "0.3.1"
```

## Extract i18n strings to .pot file

[scala-xgettext](https://github.com/xitrum-framework/scala-xgettext) 
can be used to extract strings to a .pot file directly from the code base:

* Add this to your `build.sbt` file: 
```
autoCompilerPlugins := true
addCompilerPlugin("tv.cntt" %% "xgettext" % "1.5.1")
scalacOptions ++= Seq(
  "com.github.fbaierl.i18nrx.Translator", "t:t", "t:tx", "tc:tc", "tc:tcx", "tn:tn", "tn:tnx", "tcn:tcn", "tcn:tcnx"
).map("-P:xgettext:" + _)
```
* Clean your Scala project to force the recompilation of all Scala source code files.
* Create an empty i18n.pot file in the current working directory.
* Compile your project (e.g. with `fastOptJS`) like usual. The previously created .pot file will be filled
  filled with i18n string resources extracted from compiled Scala source code files.


## License
Copyright 2018 Florian Baierl

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
