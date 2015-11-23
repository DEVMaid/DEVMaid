import com.devmaid.web._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new MyScalatraServlet, "/*")

    // Let's set the environment
    context.initParameters("org.scalatra.environment") = "production"
  }
}
