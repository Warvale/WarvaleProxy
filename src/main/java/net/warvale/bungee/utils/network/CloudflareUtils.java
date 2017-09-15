package net.warvale.bungee.utils.network;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import net.warvale.api.exceptions.HTTPRequestFailException;
import net.warvale.api.libraries.HTTPCommon;
import net.warvale.bungee.WarvaleProxy;

import java.util.List;

public class CloudflareUtils {

    public static String getRecID(String content) throws HTTPRequestFailException {
        String urlString = "https://www.cloudflare.com/api_json.html?a=rec_load_all&z=warvale.net&tkn=" +
                WarvaleProxy.cloudflareKey + "&email=" + WarvaleProxy.cloudflareEmail;
        JSONObject json = new JSONObject();

        JSONObject response = HTTPCommon.executePOSTRequest(urlString, json, 60000);
        if (response != null) {
            if (response.containsKey("response")) {
                JSONObject records = (JSONObject) response.get("response");
                if (records.containsKey("recs")) {
                    records = (JSONObject) records.get("recs");
                    if (records.containsKey("objs")) {
                        JSONArray array = (JSONArray) records.get("objs");

                        for (JSONObject record : (List<JSONObject>) array) {
                            if (((String) record.get("content")).equals(content)) {
                                return (String) record.get("rec_id");
                            }
                        }
                    } else {
                        return "-2";
                    }
                } else {
                    return "-2";
                }
            } else {
                return "-2";
            }
        } else {
            return "-2";
        }
        return "-1";
    }

    public static int getJSONInteger(JSONObject jsonObject, String key) {
        return getObjectInteger(jsonObject.get(key));
    }

    public static int getObjectInteger(Object object) {
        if (object instanceof Integer) {
            return (int) object;
        } else if (object instanceof Long) {
            return (int) ((long) object);
        }

        throw new RuntimeException("Invalid integer given " + object);
    }

}
