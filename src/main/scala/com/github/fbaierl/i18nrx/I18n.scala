package com.github.fbaierl.i18nrx

import rx.{Ctx, Rx}

object I18n extends Translator {

  /**
    * Changes the language to display.
    *
    * @param locale the language to display
    */
  def changeLanguage(locale: Locale): Unit = engine.changeLanguage(locale)

  /**
    * @return a set of all languages available
    */
  def availableLanguages: Set[Locale] = engine.availableLanguages

  /**
    * @return the currently active language
    */
  def activeLanguage: Locale = engine.activeLanguage.now

  /**
    * Loads a PO file. Adds the given language to the dictionary.
    * If a PO file with the same [[com.github.fbaierl.i18nrx.Locale]] was loaded before, the language files are merged
    * together
    *
    * @param locale      the locale of the PO file
    * @param fileContent content of the PO file
    */
  @throws(classOf[PoFileParseException])
  def loadPoFile(locale: Locale, fileContent: String): Unit = engine addPoFile(locale, fileContent)

  /**
    * The default language to display.
    */
  def defaultLanguage: Locale = Locale.en

  object activeLanguageChangedListeners {
    /**
      * Adds an observer to the active language.
      * @param listener the callback to call when the active language changes.
      */
    def +=(listener: Locale => Unit): Unit = engine addOnActiveLanguageChanged listener
  }

  object availableLanguagesChangedListeners {
    /**
      * Adds an observer to the available languages.
      * @param listener the callback to call when the available languages change.
      */
    def +=(listener: Set[Locale] => Unit): Unit = engine addOnAvailableLanguagesChanged listener
  }
}

trait Translator {

  protected val engine = TranslationEngine()

  /**
    * Translates a singular.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tx(...).apply()` so that the
    *       value gets updated automatically.
    * @param singular the text to translate
    * @return a reactive wrapping a translatable singular text
    */
  def tx(singular: String)(implicit ctx: Ctx.Owner): Rx.Dynamic[String] =
    engine createReactive((targetLanguage: Locale) => engine tc("", singular, targetLanguage))

  /**
    * Translates a singular.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tcx(...).apply()` so that the
    *       value gets updated automatically.
    * @param context the context of the text to translate
    * @param singular the text to translate
    * @return a reactive wrapping a translatable singular text determined by a context
    */
  def tcx(context: String, singular: String)(implicit ctx: Ctx.Owner): Rx.Dynamic[String] =
    engine createReactive((targetLanguage: Locale) => engine tc(context, singular, targetLanguage))

  /**
    * Translates a plural.  Automatically updates the DOM element if n is updated.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tnx(...).apply()` so that the
    *       value gets updated automatically.
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param nrx count for the plural
    * @return a reactive wrapping a translatable plural text
    */
  def tnx(singular: String, plural: String, nrx: Rx[Long])(implicit ctx: Ctx.Owner): Rx.Dynamic[String] =
    engine.createReactiveDynamicPlural(
      targetLanguage => n => engine.tcn("", singular, plural, n, targetLanguage), nrx)

  /**
    * Translates a plural with context. Automatically updates the DOM element if n is updated.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tnx(...).apply()` so that the
    *       value gets updated automatically.
    * @param context the context of the text to translate
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param nrx count for the plural
    * @return a reactive wrapping a translatable plural text
    */
  def tcnx(context: String, singular: String, plural: String, nrx: Rx[Long])(implicit ctx: Ctx.Owner): Rx.Dynamic[String] =
    engine.createReactiveDynamicPlural(
      targetLanguage => n => engine.tcn(context, singular, plural, n, targetLanguage), nrx)

  /**
    * Translates a plural.
    * @note If you use this inside a `Rx { ... }` construct you most probably want to use `tcnx(...).apply()` so that
    *       the value gets updated automatically.
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return a reactive wrapping a translatable plural text determined by a context
    */
  def tnx(singular: String, plural: String, n: Long)(implicit ctx: Ctx.Owner) : Rx.Dynamic[String] =
    engine createReactive(targetLanguage => engine.tcn("", singular, plural, n, targetLanguage))

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
  def tcnx(context: String, singular: String, plural: String, n: Long)(implicit ctx: Ctx.Owner) : Rx.Dynamic[String] =
    engine createReactive(targetLanguage => engine.tcn(context, singular, plural, n, targetLanguage))

  /**
    * Translates a singular.
    * @param singular the text to translate
    * @return the translated singular
    */
  def t(singular: String): String = engine.tc("", singular)

  /**
    * Translates a singular with context.
    * @param context the context of the text to translate
    * @param singular the text to translate
    * @return the translated singular
    */
  def tc(context: String, singular: String): String = engine.tc(context, singular)

  /**
    * Translates a plural.
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return the translated plural
    */
  def tn(singular: String, plural: String, n: Long): String = engine.tcn("", singular, plural, n)

  /**
    * Translates a plural with context.
    * @param context the context of the text to translate
    * @param singular the text to translate (singular form)
    * @param plural the text to translate (plural forms)
    * @param n count for the plural
    * @return the translated plural
    */
  def tcn(context: String, singular: String, plural: String, n: Long): String = engine.tcn(context, singular, plural, n)

}