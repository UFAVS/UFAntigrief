package com.example.ufantigrief;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

public class UFAntigrief extends JavaPlugin implements CommandExecutor {

    private com.example.ufantigrief.TheftListener theftListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        theftListener = new com.example.ufantigrief.TheftListener(this);
        getServer().getPluginManager().registerEvents(theftListener, this);
        getCommand("monitor").setExecutor(new com.example.ufantigrief.MonitorCommand(theftListener));
        getCommand("agalerts").setExecutor(new com.example.ufantigrief.MonitorCommand(theftListener));
        getCommand("agreload").setExecutor(this); // Регистрируем команду agreload

        com.example.ufantigrief.ExplosionAndFireListener explosionAndFireListener = new com.example.ufantigrief.ExplosionAndFireListener(theftListener, this);
        getServer().getPluginManager().registerEvents(explosionAndFireListener, this);
    }

    @Override
    public void onDisable() {
        // Any necessary cleanup
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("agreload")) {
            if (sender.hasPermission("ufantigrief.reload")) {
                reloadConfig(); // Перезагружаем конфигурационный файл
                theftListener.reloadPluginConfig(); // Обновляем все настройки в TheftListener
                sender.sendMessage(ChatColor.RED + "[ Защита ]" + ChatColor.GREEN + " Конфигурация успешно перезагружена!");
            } else {
                sender.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            }
            return true;
        }
        return false;
    }
}

