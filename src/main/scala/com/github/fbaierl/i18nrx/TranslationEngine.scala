package com.github.fbaierl.i18nrx

import rx.{Ctx, Rx, Var}

import scala.collection.mutable
import com.github.fbaierl.scaposer
import rx.Ctx.Owner.Unsafe._

case class TranslationEngine() {

  private[i18nrx] val defaultLanguage = Locale.en

  private[i18nrx] val activeLanguage: Var[Locale] = Var(defaultLanguage)
  activeLanguage.triggerLater { refreshReactives() }

  private[i18nrx] val availableLanguages: Var[Set[Locale]] = Var(Set())

  private[i18nrx] val i18ns: mutable.Map[Locale, scaposer.I18n] = mutable.Map[Locale, scaposer.I18n]()

  /**
    * Stores all created reactives (keys: singular and context). Context can be an empty string.
    */
  private val reactives = mutable.Map[(String, String), Rx.Dynamic[String]]()

  private[i18nrx] def createReactive(context: String, singular: String, translate: () => String)
                                    (implicit ctx: Ctx.Owner): Rx.Dynamic[String] = {
    val key = (context, singular)
    if(reactives.get(key).isDefined){
      // In case this reactive already exists re-use the old one
      reactives(key)
    } else {
      val rx = Rx { translate() }
      reactives.put(key, rx)
      rx
    }
  }

  private[i18nrx] def tc(ctx: String, singular: String): String =
    i18ns.get(activeLanguage.now).map(_.tc(ctx, singular)).getOrElse(singular)

  private[i18nrx] def tcn(context: String, singular: String, plural: String, n: Long): String =
    i18ns.get(activeLanguage.now).map(_.tcn(context, singular, plural, n)).getOrElse(singular)

  private def refreshReactives(): Unit = reactives.values.foreach(_.recalc())

  /**
    * Adds a new .po file to the dictionary
    *
    * @param locale the locale of this file
    * @param fileContent the content of the po file as plain text
    */
  @throws(classOf[PoFileParseException])
  private[i18nrx] def addPoFile(locale: Locale, fileContent: String): Unit = {
    scaposer.Parser.parse(fileContent) match {
      case Left(failure) =>
        throw PoFileParseException(s"Could not parse PO file ($locale): $failure.")
      case Right(translation) =>
        addTranslationToDictionary(locale, scaposer.I18n(translation))
        refreshReactives()
    }
  }

  private def addTranslationToDictionary(locale: Locale, i18n: scaposer.I18n): Unit = {
    if(i18ns.contains(locale)){
      i18ns.put(locale, i18ns(locale) ++ i18n)
    } else {
      i18ns.put(locale, i18n)
      availableLanguages() = i18ns.keys.toSet + defaultLanguage
      availableLanguages.recalc()
    }
  }
}
