/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package seahawkradio.cms;

import io.javalin.Javalin;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(8080);
        app.get("/", ctx -> ctx.result("Hello World"));
    }
}