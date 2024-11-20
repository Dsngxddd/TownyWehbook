package net.cengiz1.townywehbook.listener;

import net.cengiz1.townywehbook.utils.WebhookUtil;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NewTownListener implements Listener {

    private final FileConfiguration config;

    public NewTownListener(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onNewTown(NewTownEvent event) {
        String townName = event.getTown().getName();
        String mayorName = event.getTown().getMayor().getName();
        String worldName = String.valueOf(event.getTown().getHomeblockWorld());
        String title = config.getString("messages.new_town.embed_title");
        String description = config.getString("messages.new_town.embed_description")
                .replace("%town_name%", townName)
                .replace("%mayor_name%", mayorName)
                .replace("%world_name%", worldName);
        String color = config.getString("messages.new_town.embed_color", "00FF00"); // Varsayılan yeşil
        String logoUrl = config.getString("messages.new_town.embed_logo");

        String webhookUrl = config.getString("webhooks.new_town");
        String username = config.getString("webhooks.default_username", "TownyBot");
        String footerText = "";
        String footerIcon = "";
        WebhookUtil.sendWebhook(webhookUrl, username, title, description, color, logoUrl, footerText, footerIcon);
    }
}
