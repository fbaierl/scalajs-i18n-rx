package com.github.fbaierl.i18nrx

import org.scalatest._
import scalatags.JsDom.all._
import scalatags.rx.all._
import com.github.fbaierl.i18nrx.I18n._
import rx.{Ctx, Rx, Var}

class I18nSpecs extends FlatSpec {

  implicit val ctx: Ctx.Owner = Ctx.Owner.safe()

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
      String.format(tn("I have one apple", "I have {0} apples", 1), new Integer(1))
    val pluralTranslation =
      String.format(tn("I have one apple", "I have {0} apples", 2), new Integer(2))
    assert (singularTranslation == "J'ai une pomme")
    assert (pluralTranslation == "J'ai 2 pommes")
  }

  it should "update to plural form if necessary" in {
    val amountOfApples = Var(1.toLong)
    val stringFormat = tnx("I have one apple", "I have %1$s apples", amountOfApples)
    val appleString = Rx { String.format(stringFormat(), amountOfApples().toString)}
    val element = p(appleString).render

    I18n.changeLanguage(Locale.fr)
    assert (element.outerHTML == "<p>J'ai une pomme</p>")

    amountOfApples() = 2.toLong
    assert (element.outerHTML == "<p>J'ai 2 pommes</p>")

    I18n.changeLanguage(Locale.en)
    assert (element.outerHTML == "<p>I have 2 apples</p>")
  }

  it should "automatically change dom elements" in {
    I18n.changeLanguage(Locale.en)
    val element = p(title := tx("whoopsidaisies!"))(tx("Hello world")).render
    assert (element.outerHTML == "<p title=\"whoopsidaisies!\">Hello world</p>")
    I18n.changeLanguage(Locale.fr)
    assert (element.outerHTML == "<p title=\"saperlipopette!\">Bonjour monde</p>")
  }

  it should "be able to combine po files of the same locale" in {
    I18n.loadPoFile(Locale.ja, japanesePO)
    I18n.loadPoFile(Locale.ja, japaneseSpiderPo)
    I18n.changeLanguage(Locale.ja)
    val sentence = String.format(t("I like %1$s."), t("nephila clavata"))
    assert(sentence == "女郎蜘蛛が好き。")
  }

  it should "notify observers when the active language changes" in {
    var locale = Locale.en
    I18n.activeLanguageChangedListeners += {
      newLocale =>
        locale = newLocale
    }
    I18n.changeLanguage(Locale.bg)
    assert(locale === Locale.bg)
  }

  it should "notify observers when the available languages changes" in {
    var langs = I18n.availableLanguages
    assert (!I18n.availableLanguages.contains(Locale.ar_AE))
    I18n.availableLanguagesChangedListeners += {
      updatedLangs =>
        langs = updatedLangs
    }
    I18n.loadPoFile(Locale.ar_AE, "")
    assert (langs.contains(Locale.ar_AE))
  }
}
