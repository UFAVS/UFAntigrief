package com.example.ufantigrief;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookNotifier {

    private final Plugin plugin;
    private String webhookURL;

    public WebhookNotifier(Plugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.webhookURL = config.getString("webhookURL", "");
    }

    public void sendNotification(String title, String description, String player, String action, String location, String playtime) {
        if (webhookURL.isEmpty()) {
            return;  // Webhook URL не настроен
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(webhookURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = String.format("{\"embeds\":[{\"title\":\"%s\",\"description\":\"%s\",\"fields\":[{\"name\":\"Игрок\",\"value\":\"%s\",\"inline\":true},{\"name\":\"Действие\",\"value\":\"%s\",\"inline\":true},{\"name\":\"Локация\",\"value\":\"%s\",\"inline\":true},{\"name\":\"Время игры\",\"value\":\"%s\",\"inline\":true}]}]}",
                        title, description, player, action, location, playtime);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 204) {
                    plugin.getLogger().warning("Не удалось отправить Webhook: " + responseCode);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Ошибка при отправке уведомления Webhook: " + e.getMessage());
            }
        });
    }
}
