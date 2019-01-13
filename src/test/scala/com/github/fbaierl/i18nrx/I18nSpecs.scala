package com.github.fbaierl.i18nrx

import org.scalatest._
import scalatags.JsDom.all._
import scalatags.rx.all._
import rx.Ctx.Owner.Unsafe._
import rx.{Rx, Var}

class I18nSpecs extends FlatSpec {

  private val frPO =
    """
      |msgid ""
      |msgstr "Plural-Forms: nplurals=2; plural=n>1;"
      |
      |msgid "I have one apple"
      |msgid_plural "I have %1$s apples"
      |msgstr[0] "J'ai une pomme"
      |msgstr[1] "J'ai %1$s pommes"
      |
      |msgid "Hello world"
      |msgstr "Bonjour monde"
      |
      |msgid "whoopsidaisies!"
      |msgstr "saperlipopette!"
      |
      |msgid "dog"
      |msgstr "chien"
      |
      |msgid "cat"
      |msgstr "chat"
    """.stripMargin

  private val dePO =
    """
      |msgid ""
      |msgstr "Plural-Forms: nplurals=2; plural=n>1;"
      |
      |msgid "I have one apple"
      |msgid_plural "I have %1$s apples"
      |msgstr[0] "Ich habe einen Apfel"
      |msgstr[1] "Ich habe %1$s Äpfel"
    """.stripMargin

  private val japaneseSpiderPo =
    """
      |# an oriental species of golden orb-weaving spider
      |msgid "nephila clavata"
      |msgstr "女郎蜘蛛"
    """.stripMargin

  private val japanesePO =
    """
      |msgid "I like %1$s."
      |msgstr "%1$sが好き。"
    """.stripMargin

  I18n.loadPoFile(Locale.fr, frPO)
  I18n.loadPoFile(Locale.de, dePO)
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
    val element = p(
      Rx {
        val form = I18n.trx("I have one apple", "I have %1$s apples", amountOfApples)
        String.format(form(), amountOfApples().toString) }
    ).render

    assert (element.outerHTML == "<p>J'ai une pomme</p>")

    amountOfApples() = 2
    assert (element.outerHTML == "<p>J'ai 2 pommes</p>")

    I18n.changeLanguage(Locale.en)
    assert (element.outerHTML == "<p>I have 2 apples</p>")

    amountOfApples() = 3
    I18n.changeLanguage(Locale.de)
    assert (element.outerHTML == "<p>Ich habe 3 Äpfel</p>")

  }

  it should "automatically change dom elements" in {
    I18n.changeLanguage(Locale.en)
    val element = p(title := I18n.trx("whoopsidaisies!"))(I18n.trx("Hello world")).render
    assert (element.outerHTML == "<p title=\"whoopsidaisies!\">Hello world</p>")
    I18n.changeLanguage(Locale.fr)
    assert (element.outerHTML == "<p title=\"saperlipopette!\">Bonjour monde</p>")
  }

  it should "be able to combine po files of the same locale" in {
    I18n.loadPoFile(Locale.ja, japanesePO)
    I18n.loadPoFile(Locale.ja, japaneseSpiderPo)
    I18n.changeLanguage(Locale.ja)
    val sentence = String.format(I18n.t("I like %1$s."), I18n.t("nephila clavata"))
    assert(sentence == "女郎蜘蛛が好き。")
  }
}
