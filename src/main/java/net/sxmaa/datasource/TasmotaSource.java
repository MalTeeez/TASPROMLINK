package net.sxmaa.datasource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class TasmotaSource {

    public static int TIMEOUT = 3000;

    static private JsonElement queryJSONStatus() {
        try {
            URL url = new URL("http://192.168.178.21/cm?cmnd=STATUS+8");
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(false);
            huc.setConnectTimeout(TIMEOUT);
            huc.setRequestMethod("GET");
            huc.connect();

            byte[] inputData = new byte[1024];
            int readCount = readInputStreamWithTimeout(huc.getInputStream(), inputData, TIMEOUT);


            String contents = new String(inputData, 0, readCount);
            return JsonParser.parseString(contents);
        } catch (IOException e) {
            System.out.print("[" + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new java.util.Date()) + "] ");
            System.out.println("Error while getting status 8: " + e.getMessage());
        }
        return null;
    }

    static public JsonObject getTasmotaData() {
        JsonElement json = queryJSONStatus();
        if (json != null) {
            return json.getAsJsonObject().get("StatusSNS").getAsJsonObject().get("SML").getAsJsonObject();
        } else {
            return new JsonObject();
        }
    }

    public static int readInputStreamWithTimeout(InputStream is, byte[] b, int timeoutMillis)
            throws IOException  {
        int bufferOffset = 0;
        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < b.length) {
            int readLength = java.lang.Math.min(is.available(),b.length-bufferOffset);
            // can alternatively use bufferedReader, guarded by isReady():
            int readResult = is.read(b, bufferOffset, readLength);
            if (readResult == -1) break;
            bufferOffset += readResult;
        }
        return bufferOffset;
    }

}
