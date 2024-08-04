package net.sxmaa.datasource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class TasmotaSource {
    static private JsonElement queryJSONStatus() {
        try(java.io.InputStream is = new java.net.URL("http://192.168.178.21/cm?cmnd=STATUS+8").openStream()) {
            String contents = new String(is.readAllBytes());
            return JsonParser.parseString(contents);
        } catch (IOException e) {
            System.out.print(new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new java.util.Date()));
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
}
