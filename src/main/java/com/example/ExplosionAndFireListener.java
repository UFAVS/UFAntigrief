package com.example.ufantigrief;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ExplosionAndFireListener implements Listener {

    private final com.example.ufantigrief.TheftListener theftListener;
    private final Plugin plugin;

    public ExplosionAndFireListener(com.example.ufantigrief.TheftListener theftListener, Plugin plugin) {
        this.theftListener = theftListener;
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        CompletableFuture.supplyAsync(() -> getPlayerFromEntity(entity))
                .thenAcceptAsync(player -> {
                    if (player != null && theftListener.isMonitored(player)) {
                        Material material = getMaterialFromEntity(entity);
                        Location location = entity.getLocation();
                        theftListener.alertAdminsAsync(player, "создал взрыв", material, location);
                    }
                });
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        CompletableFuture.supplyAsync(() -> {
            Player player = getPlayerFromEntity(entity);
            List<Block> affectedBlocks = event.blockList();
            return new Object[]{player, affectedBlocks};
        }).thenAcceptAsync(result -> {
            Player player = (Player) result[0];
            List<Block> affectedBlocks = (List<Block>) result[1];

            if (player != null && theftListener.isMonitored(player)) {
                List<Material> destroyedValuableItems = affectedBlocks.stream()
                        .map(Block::getType)
                        .filter(com.example.ufantigrief.ValuableItems::isValuable)
                        .collect(Collectors.toList());

                if (!destroyedValuableItems.isEmpty()) {
                    Material explosiveMaterial = getMaterialFromEntity(entity);
                    Location location = entity.getLocation();
                    String destroyedItems = destroyedValuableItems.stream()
                            .map(Material::name)
                            .collect(Collectors.joining(", "));

                    theftListener.alertAdminsAsync(player, "взорвал ценные предметы: " + ChatColor.BLUE + destroyedItems, explosiveMaterial, location);
                }
            }
        });
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player != null && theftListener.isMonitored(player)) {
            Location location = event.getBlock().getLocation();
            CompletableFuture.runAsync(() -> {
                theftListener.alertAdminsAsync(player, "поджег блок", Material.FLINT_AND_STEEL, location);
            });
        }
    }

    // Метод для определения игрока из сущности
    private Player getPlayerFromEntity(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        } else if (entity instanceof Creeper || entity instanceof EnderCrystal || entity instanceof ExplosiveMinecart) {
            return getNearestPlayer(entity.getLocation());
        }
        return null;
    }

    // Метод для определения материала из сущности
    private Material getMaterialFromEntity(Entity entity) {
        if (entity instanceof Player) {
            return Material.PLAYER_HEAD;  // Игрок создаёт взрыв
        } else if (entity instanceof Creeper) {
            return Material.CREEPER_HEAD;  // Крипер
        } else if (entity instanceof EnderCrystal) {
            return Material.END_CRYSTAL;  // Кристалл
        } else if (entity instanceof ExplosiveMinecart) {
            return Material.TNT_MINECART;  // Вагонетка с динамитом
        }
        return Material.TNT;  // По умолчанию для других взрывов
    }

    // Метод для получения ближайшего игрока к месту взрыва
    private Player getNearestPlayer(Location location) {
        return location.getWorld().getPlayers().stream()
                .min((p1, p2) -> Double.compare(p1.getLocation().distance(location), p2.getLocation().distance(location)))
                .orElse(null);
    }
}
