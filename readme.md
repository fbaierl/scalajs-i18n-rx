[![Build Status](https://travis-ci.org/fbaierl/scalajs-i18n-rx.svg?branch=master)](https://travis-ci.org/fbaierl/scalajs-i18n-rx) 
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg)](https://www.scala-js.org)
# scalajs-i18n-rx

*Change the language of your entire web app with one line of code*:

```
I18n.changeLanguage(Locale.de)
```

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

// a minimal example of a PO file
val dePoFile = """
msgid "Hello World"
msgstr "Hallo Welt"
"""

// load po files
I18n.loadPoFile(Locale.de, dePoFile)

// dom creation
div(p(I18n.trx("Hello World")))
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

```scala
import scalatags.JsDom.all._
import scalatags.rx.all._
import com.github.fbaierl.i18nrx._
import rx.Ctx.Owner.Unsafe._

val frPoFile = """
msgid ""
msgstr "Plural-Forms: nplurals=2; plural=n>1;"

msgid "I have one apple"
msgid_plural "I have %1$s" apples"
msgstr[0] "J'ai une pomme"
msgstr[1] "J'ai %1$s" pommes"
"""

I18n.loadPoFile(Locale.fr, frPoFile)

val singular = String.format(I18n.t("I have one apple", "I have {0} apples", 1), new Integer(1))
val singularP = p(singular).render

val plural = String.format(I18n.t("I have one apple", "I have {0} apples", 2), new Integer(2))
val pluralP = p(plural).render

println(singularP.innerHTML) // "I have one apple"
println(pluralP.innerHTML) // "I have {0} apples"

I18n.changeLanguage(Locale.fr)

println(singularP.innerHTML) // "J'ai une pomme"
println(pluralP.innerHTML) // "J'ai {0} pommes"
```

If the number deciding which plural form will be used (`n`) itself is an `Rx` it can be used directly like this:

```scala
val amountOfApples = Var(1.toLong) // n
val rx = I18n.trx("I have one apple", "I have {0} apples", amountOfApples)
val element = p(Rx { String.format(rx(), amountOfApples().toString) }).render

println(element.innerHTML) // "J'ai une pomme"
amountOfApples() = 2
println(element.innerHTML) // "J'ai 2 pommes"
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
  * @param singular the text to translate
  * @return a reactive wrapping a translatable singular text
  */
def trx(singular: String)(implicit ctx: Ctx.Owner): Rx.Dynamic[String]

/**
  * Translates a singular.
  * @param context the context of the text to translate
  * @param singular the text to translate
  * @return a reactive wrapping a translatable singular text determined by a context
  */
def trx(context: String, singular: String)(implicit ctx: Ctx.Owner): Rx.Dynamic[String] 

/**
  * Translates a plural.
  * @param singular the text to translate (singular form)
  * @param plural the text to translate (plural forms)
  * @param n count for the plural
  * @return a reactive wrapping a translatable plural text
  */
def trx(singular: String, plural: String, n: Long)(implicit ctx: Ctx.Owner): Rx.Dynamic[String]

/**
  * Translates a plural. Automatically updates the DOM element if n is updated.
  * @param singular the text to translate (singular form)
  * @param plural the text to translate (plural forms)
  * @param n count for the plural (a Rx)
  * @return a reactive wrapping a translatable plural text
  */
def trx(singular: String, plural: String, n: Rx[Long])(implicit ctx: Ctx.Owner): Rx.Dynamic[String]

/**
  * Translates a plural with context.
  * @param context the context of the text to translate
  * @param singular the text to translate (singular form)
  * @param plural the text to translate (plural forms)
  * @param n count for the plural
  * @return a reactive wrapping a translatable plural text determined by a context
  */
def trx(context: String, singular: String, plural: String, n: Long)(implicit ctx: Ctx.Owner) : Rx.Dynamic[String]

/**
  * Translates a plural with context. Automatically updates the DOM element if n is updated.
  * @param context the context of the text to translate
  * @param singular the text to translate (singular form)
  * @param plural the text to translate (plural forms)
  * @param n count for the plural (a Rx)
  * @return a reactive wrapping a translatable plural text determined by a context
  */
def trx(context: String, singular: String, plural: String, n: Rx[Long])(implicit ctx: Ctx.Owner) : Rx.Dynamic[String]

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
def t(context: String, singular: String): String

/**
  * Translates a plural.
  * @param singular the text to translate (singular form)
  * @param plural the text to translate (plural forms)
  * @param n count for the plural
  * @return the translated plural
  */
def t(singular: String, plural: String, n: Long): String

/**
  * Translates a plural with context.
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

```scala
libraryDependencies += "com.github.fbaierl" %%% "scalajs-i18n-rx" % "0.2"
```

## License
Copyright 2018 Florian Baierl

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.