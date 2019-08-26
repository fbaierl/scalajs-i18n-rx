package com.github.fbaierl.i18nrx

import com.github.fbaierl.scaposer
import rx.{Ctx, Rx, Var}

import scala.collection.mutable

case class TranslationEngine() {

  private var onActiveLanguageChangedListeners: Seq[Locale => Unit] = Seq()
  private var onAvailableLanguagesChangedListeners: Seq[Set[Locale] => Unit] = Seq()

  private[i18nrx] def addOnActiveLanguageChanged(callback: Locale => Unit): Unit =
    onActiveLanguageChangedListeners = onActiveLanguageChangedListeners :+ callback

  private[i18nrx] def addOnAvailableLanguagesChanged(callback: Set[Locale] => Unit): Unit =
    onAvailableLanguagesChangedListeners = onAvailableLanguagesChangedListeners :+ callback

  private[i18nrx] var defaultPluralDetector: Long => Boolean = n => n != 1

  private[i18nrx] val defaultLanguage = Locale.en

  private[i18nrx] var activeLanguage: Var[Locale] = Var(defaultLanguage)

  private[i18nrx] def changeLanguage(newLanguage: Locale): Unit = {
    activeLanguage() = newLanguage
    onActiveLanguageChangedListeners.foreach(_(activeLanguage.now))
  }

  private[i18nrx] var _availableLanguages: Set[Locale] = Set()
  private[i18nrx] def availableLanguages: Set[Locale] = _availableLanguages
  private[i18nrx] def availableLanguages_=(value: Set[Locale]): Unit = {
    _availableLanguages = value
    onAvailableLanguagesChangedListeners foreach { _(availableLanguages) }
  }

  private[i18nrx] val i18ns: mutable.Map[Locale, scaposer.I18n] = mutable.Map[Locale, scaposer.I18n]()

  private[i18nrx] def createReactive(translate: Locale => String)
                                    (implicit ctx: Ctx.Owner): Rx.Dynamic[String] =  Rx { translate(activeLanguage()) }

  private[i18nrx] def createReactiveDynamicPlural(translate: Locale => Long => String,
                                                  nrx: Rx[Long])
                                                 (implicit ctx: Ctx.Owner): Rx.Dynamic[String] =
    Rx { translate(activeLanguage())(nrx()) }

  private[i18nrx] def tc(ctx: String, singular: String, targetLanguage: Locale = activeLanguage.now): String =
    i18ns.get(targetLanguage).map(_.tc(ctx, singular)).getOrElse(singular)

  private[i18nrx] def tcn(context: String, singular: String, plural: String, n: Long, targetLanguage: Locale = activeLanguage.now): String =
    i18ns.get(targetLanguage).map(_.tcn(context, singular, plural, n))
      .getOrElse(if(defaultPluralDetector(n)) plural else singular)

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
        activeLanguage.recalc() // triggers recalculation of previously added tags
    }
  }

  private def addTranslationToDictionary(locale: Locale, i18n: scaposer.I18n): Unit = {
    if(i18ns.contains(locale)){
      i18ns.put(locale, i18ns(locale) ++ i18n)
    } else {
      i18ns.put(locale, i18n)
      availableLanguages = i18ns.keys.toSet + defaultLanguage
    }
  }
}