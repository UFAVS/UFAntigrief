package com.example.ufantigrief;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TheftListener implements Listener {

    private final JavaPlugin plugin;
    private final com.example.ufantigrief.WebhookNotifier webhookNotifier;
    private final Set<UUID> monitoredPlayers = new HashSet<>();
    private final Set<UUID> whitelistPlayers = new HashSet<>();
    private final Set<UUID> disabledAlertsPlayers = new HashSet<>();
    private long minPlayTimeThreshold;
    private final int violationThreshold;
    private final Map<UUID, Integer> playerViolations = new HashMap<>();
    private final File whitelistFile;
    private final FileConfiguration whitelistConfig;
    private String teleportCommand;
    private String banCommand;

    public TheftListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.webhookNotifier = new com.example.ufantigrief.WebhookNotifier(plugin);
        reloadPluginConfig();
        FileConfiguration config = plugin.getConfig();

        minPlayTimeThreshold = config.getLong("minPlayTimeHours") * 60 * 60 * 20; // Пороговое время в тиках (часы * 60 мин * 60 сек * 20 тиков)
        violationThreshold = config.getInt("violationThreshold", 3); // Порог нарушений (по умолчанию 3)

        whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml");
        if (!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        whitelistConfig = YamlConfiguration.loadConfiguration(whitelistFile);
        loadWhitelist();

        // Загрузка команды телепортации из конфигурации
        teleportCommand = config.getString("teleportCommand", "/tp {player} {x} {y} {z}");
    }

    public void updatePlayTimeThreshold() {
        this.minPlayTimeThreshold = plugin.getConfig().getLong("minPlayTimeHours") * 60 * 60 * 20;
    }

    private void loadWhitelist() {
        for (String entry : whitelistConfig.getStringList("whitelistedPlayers")) {
            String[] parts = entry.split(":");
            UUID uuid = UUID.fromString(parts[0]);
            whitelistPlayers.add(uuid);
        }
    }

    private void saveWhitelist() {
        whitelistConfig.set("whitelistedPlayers", whitelistPlayers.stream()
                .map(uuid -> uuid.toString() + ":" + Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toList()));
        try {
            whitelistConfig.save(whitelistFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadPluginConfig() {
        plugin.reloadConfig(); // Перезагружаем конфигурационный файл
        FileConfiguration config = plugin.getConfig();

        // Обновляем все настройки из конфигурации
        this.minPlayTimeThreshold = config.getLong("minPlayTimeHours") * 60 * 60 * 20;
        this.teleportCommand = config.getString("teleportCommand", "/tp {player} {x} {y} {z}");
        this.banCommand = config.getString("banCommand", "ban {player} Подозрительное поведение");
        this.webhookNotifier.reloadConfig(); // Обновляем URL вебхука        // Добавьте сюда перезагрузку других настроек по мере необходимости
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!monitoredPlayers.contains(player.getUniqueId())) {
            monitoredPlayers.add(player.getUniqueId());
        }
    }

    public void addToWhitelist(Player player) {
        whitelistPlayers.add(player.getUniqueId());
        saveWhitelist();
    }

    public void removeFromWhitelist(Player player) {
        whitelistPlayers.remove(player.getUniqueId());
        saveWhitelist();
    }

    public void enableAlerts(Player player) {
        disabledAlertsPlayers.remove(player.getUniqueId());
        player.sendMessage(ChatColor.RED + "[ Защита ]" + ChatColor.LIGHT_PURPLE + " Уведомления включены.");
    }

    public void disableAlerts(Player player) {
        disabledAlertsPlayers.add(player.getUniqueId());
        player.sendMessage(ChatColor.RED + "[ Защита ]" + ChatColor.LIGHT_PURPLE + " Уведомления отключены.");
    }

    private boolean alertsEnabled(Player player) {
        return !disabledAlertsPlayers.contains(player.getUniqueId());
    }

    boolean isMonitored(Player player) {
        int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        boolean isBelowThreshold = playTimeTicks < minPlayTimeThreshold;
        return monitoredPlayers.contains(player.getUniqueId()) && !whitelistPlayers.contains(player.getUniqueId()) && isBelowThreshold;
    }

    CompletableFuture<Void> alertAdminsAsync(Player player, String action, Material material, Location location) {
        return CompletableFuture.runAsync(() -> {
            String message = ChatColor.RED + "[ Защита ] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.WHITE + " " +
                    action + " " + ChatColor.AQUA + material.name() + ChatColor.WHITE + " на координатах " +
                    ChatColor.YELLOW + locationToString(location) + ChatColor.WHITE +
                    " (Время игры: " + ChatColor.GREEN + getFormattedPlayTime(player) + ChatColor.WHITE + ")";
            boolean moderatorOnline = false;

            for (Player admin : Bukkit.getOnlinePlayers()) {
                if (admin.hasPermission("ufantigrief.notify") && !disabledAlertsPlayers.contains(admin.getUniqueId())) {
                    TextComponent textComponent = new TextComponent(message);
                    TextComponent tpComponent = new TextComponent(ChatColor.LIGHT_PURPLE + " [Телепорт]");
                    // Заменяем placeholders в команде телепортации
                    String tpCommand = teleportCommand
                            .replace("{player}", admin.getName())
                            .replace("{x}", String.valueOf(location.getX()))
                            .replace("{y}", String.valueOf(location.getY()))
                            .replace("{z}", String.valueOf(location.getZ()));
                    tpComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, tpCommand));
                    admin.spigot().sendMessage(textComponent, tpComponent);

                    moderatorOnline = true;
                }
            }

            if (!moderatorOnline) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    int violations = playerViolations.getOrDefault(player.getUniqueId(), 0) + 1;
                    playerViolations.put(player.getUniqueId(), violations);

                    if (violations >= violationThreshold) {
                        String banCommandToExecute = banCommand.replace("{player}", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCommandToExecute);
                    }
                });
            }

            // Отправка уведомления в Webhook
            String formattedLocation = locationToString(location);
            String formattedPlayTime = getFormattedPlayTime(player);
            webhookNotifier.sendNotification(
                    "Уведомление о нарушении",
                    "Детали нарушения",
                    player.getName(),
                    action + " " + material.name(),
                    formattedLocation,
                    formattedPlayTime
            );
        }, plugin.getServer().getScheduler().getMainThreadExecutor(plugin));
    }

    private String getFormattedPlayTime(Player player) {
        int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int playTimeHours = playTimeTicks / (20 * 60 * 60);
        int playTimeMinutes = (playTimeTicks / (20 * 60)) % 60;
        return playTimeHours + "ч " + playTimeMinutes + "мин";
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + " " +
                location.getBlockX() + " " +
                location.getBlockY() + " " +
                location.getBlockZ();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isMonitored(player) && com.example.ufantigrief.ValuableItems.isValuable(event.getBlock().getType())) {
            Material blockType = event.getBlock().getType();
            if (blockType != Material.AIR) {
                alertAdminsAsync(player, "сломал", blockType, event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isMonitored(player) && com.example.ufantigrief.ValuableItems.isValuable(event.getBlock().getType())) {
            Material blockType = event.getBlock().getType();
            if (blockType != Material.AIR) {
                alertAdminsAsync(player, "поставил", blockType, event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (isMonitored(player) && event.getInventory().getType() != InventoryType.CRAFTING) {
                Material currentItemMaterial = event.getCurrentItem() != null ? event.getCurrentItem().getType() : Material.AIR;
                Material cursorItemMaterial = event.getCursor() != null ? event.getCursor().getType() : Material.AIR;
                if (com.example.ufantigrief.ValuableItems.isValuable(currentItemMaterial) ||
                        com.example.ufantigrief.ValuableItems.isValuable(cursorItemMaterial)) {
                    Location location = player.getLocation();
                    alertAdminsAsync(player, "взаимодействует с", currentItemMaterial != Material.AIR ? currentItemMaterial : cursorItemMaterial, location);
                }
            }
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isMonitored(player) && com.example.ufantigrief.ValuableItems.isValuable(event.getItem().getItemStack().getType())) {
                Material itemType = event.getItem().getItemStack().getType();
                if (itemType != Material.AIR) {
                    alertAdminsAsync(player, "подобрал", itemType, player.getLocation());
                }
            }
        }
    }
}
