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

## Basic Usage

```scala
// First of all some necessary imports
import scalatags.JsDom.all._
import scalatags.rx.all._
import com.github.fbaierl.i18nrx._
import rx.Ctx.Owner.Unsafe._

```
````scala
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

```scala
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
    * @param singular the text to translate
    * @return a reactive wrapping a translatable singular text
    */
  def trx(singular: String)(implicit ctx: Ctx.Owner): Rx.Dynamic[String]

  /**
    * @param context the context of the text to translate
    * @param singular the text to translate
    * @return a reactive wrapping a translatable singular text determined by a context
    */
  def trx(context: String, singular: String)(implicit ctx: Ctx.Owner): Rx.Dynamic[String]

  /**
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return a reactive wrapping a translatable plural text
    */
  def trx(singular: String, plural: String, n: Long)(implicit ctx: Ctx.Owner): Rx.Dynamic[String]

  /**
    * @param context the context of the text to translate
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return a reactive wrapping a translatable plural text determined by a context
    */
  def trx(context: String, singular: String, plural: String, n: Long)(implicit ctx: Ctx.Owner) : Rx.Dynamic[String]

  /**
    * @param singular the text to translate
    * @return the translated singular
    */
  def t(singular: String): String

  /**
    * @param context the context of the text to translate
    * @param singular the text to translate
    * @return the translated singular
    */
  def t(context: String, singular: String): String

  /**
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return the translated plural
    */
  def t(singular: String, plural: String, n: Long): String

  /**
    * @param context the context of the text to translate
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return the translated plural
    */
  def t(context: String, singular: String, plural: String, n: Long): String
```

## Installation

build.sbt example:
TODO NOT RELEASED YET
```scala
libraryDependencies += "com.github.fbaierl" %%% "scalajs-i18n-rx" % "0.1"
```