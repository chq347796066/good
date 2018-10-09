package com.spark.recommend.webservice


import org.glassfish.jersey.servlet.ServletContainer
import org.spark_project.jetty.server.Server
import org.spark_project.jetty.servlet.{ServletContextHandler, ServletHolder}

object RecoServer {
  def start {
    val context: ServletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS)
    context.setContextPath("/")
    val webServer = new Server(9999)
    webServer.setHandler(context)
    val jerseyServlet: ServletHolder = context.addServlet(classOf[ServletContainer], "/*")
    jerseyServlet.setInitOrder(0)
    jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.spark.recommend.webservice")
    try {
      println("Web Server started ......")
      webServer.start
      webServer.join
    } catch {
      case e: Exception => {
        e.printStackTrace
        println("ERROR:" + e)
      }
      case e: Throwable => println("ERROR:" + e)
    } finally {
      webServer.destroy
    }
  }

  @throws(classOf[Exception])
  def main(args: Array[String]) {
    RecoServer.start
  }
}
