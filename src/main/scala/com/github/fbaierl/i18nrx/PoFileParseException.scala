package com.github.fbaierl.i18nrx

final case class PoFileParseException(private val message: String = "",
                                       private val cause: Throwable = None.orNull) extends Exception(message, cause)

