package net.cengiz1.townywehbook.listener;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import com.palmergames.bukkit.towny.object.Town;
import net.cengiz1.townywehbook.TownyWehbook;
import net.cengiz1.townywehbook.utils.WebhookUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NewDayListener implements Listener {

    private final FileConfiguration config;
    private final TownyWehbook plugin;

    public NewDayListener(FileConfiguration config, TownyWehbook plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onNewDay(NewDayEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            Collection<Town> towns = TownyUniverse.getInstance().getTowns();

            Town richestTown = towns.stream()
                    .max(Comparator.comparingDouble(town -> town.getAccount().getHoldingBalance()))
                    .orElse(null);

            Town poorestTown = towns.stream()
                    .min(Comparator.comparingDouble(town -> town.getAccount().getHoldingBalance()))
                    .orElse(null);

            double totalTaxes = towns.stream()
                    .mapToDouble(town -> {
                        try {
                            return town.getTaxes();
                        } catch (Exception e) {
                            return 0.0;
                        }
                    })
                    .sum();

            List<String> unpaidTowns = towns.stream()
                    .filter(town -> town.getAccount().getHoldingBalance() < town.getTaxes())
                    .map(Town::getName)
                    .collect(Collectors.toList());

            String title = config.getString("messages.new_day_report.title", "Daily Report");
            String totalTaxesMsg = config.getString("messages.new_day_report.total_taxes", "Total Taxes: %total_taxes%")
                    .replace("%total_taxes%", String.format("%.2f", totalTaxes));

            String richestMsg = richestTown != null
                    ? config.getString("messages.new_day_report.richest_town", "Richest Town: %richest_town% (%richest_balance%)")
                    .replace("%richest_town%", richestTown.getName())
                    .replace("%richest_balance%", String.format("%.2f", richestTown.getAccount().getHoldingBalance()))
                    : "";

            String poorestMsg = poorestTown != null
                    ? config.getString("messages.new_day_report.poorest_town", "Poorest Town: %poorest_town% (%poorest_balance%)")
                    .replace("%poorest_town%", poorestTown.getName())
                    .replace("%poorest_balance%", String.format("%.2f", poorestTown.getAccount().getHoldingBalance()))
                    : "";

            String unpaidMsg = unpaidTowns.isEmpty()
                    ? config.getString("messages.new_day_report.all_paid", "All taxes have been paid.")
                    : config.getString("messages.new_day_report.unpaid_towns", "Unpaid Taxes: %unpaid_towns%")
                    .replace("%unpaid_towns%", String.join(", ", unpaidTowns));

            String webhookUrl = config.getString("webhooks.new_day", "");
            String embedTitle = config.getString("messages.new_day_report.embed_title", "Daily Towny Report");
            String embedDescription = title + "\n" + totalTaxesMsg + "\n" + richestMsg + "\n" + poorestMsg + "\n" + unpaidMsg;
            String logoUrl = config.getString("webhooks.embed_logo_url", "");
            String username = config.getString("webhooks.default_username", "TownyBot");

            WebhookUtil.sendWebhook(webhookUrl, username, embedTitle, embedDescription, "00FF00", logoUrl, "", "");
        }, config.getInt("general.update_interval_ticks", 20));
    }
}
