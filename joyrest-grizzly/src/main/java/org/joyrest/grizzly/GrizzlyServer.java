package org.joyrest.grizzly;

import org.glassfish.grizzly.http.server.HttpServer;
import org.joyrest.context.ApplicationContext;
import org.joyrest.grizzly.handler.GrizzlyApplicationHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

public class GrizzlyServer {

    private static Logger LOG = Logger.getLogger(GrizzlyServer.class.getName());

    public static void start(final ApplicationContext applicationConfig, final int port, String path) {
        try {
            // Logging settings
            InputStream loggingStream = GrizzlyServer.class.getResourceAsStream("/logging.properties");
            if (nonNull(loggingStream))
                LogManager.getLogManager().readConfiguration(loggingStream);

            HttpServer server = HttpServer.createSimpleServer(null, port);
            server.getServerConfiguration().addHttpHandler(
                    new GrizzlyApplicationHandler(applicationConfig), path);

            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

            server.start();
            LOG.info("Grizzly Server started. Stop the application using ^C.");
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

}
