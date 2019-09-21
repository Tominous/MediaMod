package me.conorthedev.mediamod.media.browser;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.conorthedev.mediamod.media.base.IMediaHandler;
import me.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * The class which receives data from the browser extension
 *
 * @author ConorTheDev
 */
public class BrowserHandler implements IMediaHandler {
    /**
     * Instance of the MediaHandler
     */
    public static final BrowserHandler INSTANCE = new BrowserHandler();

    /**
     * Logger used to log info messages, debug messages, error messages & more
     *
     * @see org.apache.logging.log4j.Logger
     */
    private static final Logger LOGGER = LogManager.getLogger("MediaMod - BrowserHandler");

    /**
     * If the handler has been initialized or not
     */
    private static Boolean INITIALIZED = false;

    /**
     * The MediaHandler Server
     */
    private HttpServer server = null;

    /**
     * The current track
     */
    public CurrentlyPlayingObject currentTrack;

    /**
     * Start the Media Handler, listen for requests from the extension, etc.
     */
    @Override
    public void initializeHandler() throws HandlerInitializationException {
        if (INITIALIZED) {
            LOGGER.warn("Attempt to initialize Media Handler when it was already initialized?");
        }

        try {
            LOGGER.info("Initializing Media Handler");

            // Create a HTTP Server for the extension to send requests to (http://localhost:9102)
            server = HttpServer.create(new InetSocketAddress(9102), 0);
            server.setExecutor(null);
            server.createContext("/", new ConnectionCallbackHandler());

            // Start the server
            server.start();
            LOGGER.info("Server started on port 9102");
        } catch (IOException e) {
            throw new HandlerInitializationException(e);
        }

        LOGGER.info("Browser Handler initialized and ready!");

        INITIALIZED = true;
    }

    @Override
    public String getHandlerName() {
        return "Browser Extension";
    }

    @Override
    public CurrentlyPlayingObject getCurrentTrack() {
        return BrowserHandler.INSTANCE.currentTrack;
    }

    @Override
    public boolean handlerReady() {
        return BrowserHandler.INSTANCE.currentTrack != null;
    }

    /**
     * The connection callback handler
     */
    private static class ConnectionCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                t.sendResponseHeaders(204, -1);
                return;
            }

            String requestBody = IOUtils.toString(t.getRequestBody());

            Gson g = new Gson();

            BrowserHandler.INSTANCE.currentTrack = g.fromJson(requestBody, CurrentlyPlayingObject.class);
            System.out.println(BrowserHandler.INSTANCE.currentTrack.item.name + " by " + BrowserHandler.INSTANCE.currentTrack.item.album.artists[0].name);

            String response = "Success";

            t.sendResponseHeaders(200, response.length());

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
