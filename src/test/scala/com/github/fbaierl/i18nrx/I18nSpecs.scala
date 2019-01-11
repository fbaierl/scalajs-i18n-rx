package com.github.fbaierl.i18nrx

import org.scalatest._
import scalatags.JsDom.all._
import scalatags.rx.all._
import rx.Ctx.Owner.Unsafe._
import rx.{Rx, Var}

class I18nSpecs extends FlatSpec {

  val po = """
msgid ""
msgstr "Plural-Forms: nplurals=2; plural=n>1;"

msgid "I have one apple"
msgid_plural "I have %1$s apples"
msgstr[0] "J'ai une pomme"
msgstr[1] "J'ai %1$s pommes"

msgid "Hello world"
msgstr "Bonjour monde"

msgid "whoopsidaisies!"
msgstr "saperlipopette!"

msgid "dog"
msgstr "chien"

msgid "cat"
msgstr "chat"
"""

  val dePo = """
msgid ""
msgstr ""
"Plural-Forms: nplurals=2; plural=(n!=1);\n"

msgid "This rack currently carries {0} item."
msgid_plural "This rack currently carries {0} items."
msgstr[0] "Dieses Transportmittel trägt momentan {0} Teil."
msgstr[1] "Dieses Transportmittel trägt momentan {0} Teile."
"""

  I18n.loadPoFile(Locale.fr, po)
  I18n.loadPoFile(Locale.de, po)
  I18n.changeLanguage(Locale.fr)

  "I18n" should "be able to translate plurals" in {
    val singularTranslation =
      String.format(I18n.t("I have one apple", "I have {0} apples", 1), new Integer(1))
    val pluralTranslation =
      String.format(I18n.t("I have one apple", "I have {0} apples", 2), new Integer(2))
    assert (singularTranslation == "J'ai une pomme")
    assert (pluralTranslation == "J'ai 2 pommes")
  }

  it should "update to plural form if necessary" in {
    val amountOfApples = Var(new Integer(1))
    val stringFormat = Rx { I18n.t("I have one apple", "I have {0} apples", amountOfApples().toInt) }
    val element = p(Rx { String.format(stringFormat(), amountOfApples()) }).render
    assert (element.outerHTML == "<p>J'ai une pomme</p>")
    amountOfApples() = new Integer(2)
    assert (element.outerHTML == "<p>J'ai 2 pommes</p>")
  }

  it should "update to plural form if n is a reactive" in {
    val amountOfApples = Var(1.toLong)
    val rx = I18n.trx("I have one apple", "I have {0} apples", amountOfApples)
    val element = p(Rx { String.format(rx(), amountOfApples().toString) }).render

    assert (element.outerHTML == "<p>J'ai une pomme</p>")
    amountOfApples() = 2
    assert (element.outerHTML == "<p>J'ai 2 pommes</p>")
    amountOfApples() = 1
    assert (element.outerHTML == "<p>J'ai une pomme</p>")
  }

  it should "automatically change dom elements" in {
    I18n.changeLanguage(Locale.en)
    val element = p(title := I18n.trx("whoopsidaisies!"))(I18n.trx("Hello world")).render
    assert (element.outerHTML == "<p title=\"whoopsidaisies!\">Hello world</p>")
    I18n.changeLanguage(Locale.fr)
    assert (element.outerHTML == "<p title=\"saperlipopette!\">Bonjour monde</p>")
  }

}
