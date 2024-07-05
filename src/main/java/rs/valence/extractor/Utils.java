package rs.valence.extractor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Utils {
    private static final String MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";

    private static final Gson gson = new Gson();

    public static JsonObject getVersionManifest(String version) {
        try {
            URL url = new URL(MANIFEST_URL);
            URLConnection connection = url.openConnection();
            connection.connect();

            JsonObject manifest = gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);

            for (JsonElement element : manifest.getAsJsonArray("versions").getAsJsonArray()) {
                JsonObject versionObject = element.getAsJsonObject();
                if (versionObject.get("id").getAsString().equals(version)) {
                    return versionObject;
                }
            }

            throw new RuntimeException("Version not found: " + version);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL: " + MANIFEST_URL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject getVersionJson(String version) {
        String sUrl = getVersionManifest(version).get("url").getAsString();
        try {
            URL url = new URL(sUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            return gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL: " + sUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void downloadJar(String version) {
        String sUrl = getVersionJson(version).getAsJsonObject("downloads").getAsJsonObject("server").get("url").getAsString();
        try {
            URL url = new URL(sUrl);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream("server.jar");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL: " + sUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}