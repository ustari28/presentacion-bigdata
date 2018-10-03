package com.alan.developer.bigdata


import org.slf4j.{Logger, LoggerFactory}

/**
  * Logging application.
  *
  */
object AppicationLogging {
  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    log.info("We don't support kafka in 1.6")
  }
}
