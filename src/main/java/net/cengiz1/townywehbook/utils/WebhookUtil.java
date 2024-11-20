package net.cengiz1.townywehbook.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

public class WebhookUtil {

    private static final OkHttpClient client = new OkHttpClient();

    public static void sendWebhook(String webhookUrl, String username, String title, String description, String color, String logoUrl, String footerText, String footerIcon) {
        if (footerText == null) footerText = "";
        if (footerIcon == null) footerIcon = "";

        int parsedColor = 0xFFFFFF;
        if (color != null && !color.isEmpty()) {
            try {
                parsedColor = Integer.parseInt(color, 16);
            } catch (NumberFormatException e) {
                System.out.println("Geçersiz renk kodu: " + color);
                parsedColor = 0xFFFFFF;
            }
        }
        JsonObject embed = new JsonObject();
        embed.addProperty("title", title);
        embed.addProperty("description", description);
        embed.addProperty("color", parsedColor);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            JsonObject thumbnail = new JsonObject();
            thumbnail.addProperty("url", logoUrl);
            embed.add("thumbnail", thumbnail);
        }

        JsonObject footer = new JsonObject();
        footer.addProperty("text", footerText);
        footer.addProperty("icon_url", footerIcon);
        embed.add("footer", footer);

        JsonArray embeds = new JsonArray();
        embeds.add(embed);

        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.add("embeds", embeds);
        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                System.out.println("Webhook gönderilemedi: " + response.code() + " - " + response.body().string());
            } else {
                System.out.println("Webhook başarıyla gönderildi.");
            }
        } catch (Exception e) {
            System.out.println("Webhook gönderme hatası: " + e.getMessage());
        }
    }
}
