package net.mccg.web

import akka.actor.ActorSystem
import net.mccg.common.MCCGConfig

import scala.concurrent.Future
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

object MCCGMain extends App {
  implicit val system = ActorSystem("test")
  //implicit val materializer = ActorMaterializer()
  implicit  val ec = system.dispatcher

  val serverSource = Http().bind(interface = "0.0.0.0", port = MCCGConfig().port.get)


  val requestHandler: HttpRequest => Future[HttpResponse] = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      Future(HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,
        "<html><body>Hello world!</body></html>")))

    case _: HttpRequest =>
      Future(HttpResponse(404, entity = "Unknown resource!"))
  }

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      connection handleWithAsyncHandler  requestHandler
    }).run()

  bindingFuture onFailure  {
    case t => println("An error has occured: " + t.getMessage)
  }

}
