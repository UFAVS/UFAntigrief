package com.example.ufantigrief;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonitorCommand implements CommandExecutor {

    private final com.example.ufantigrief.TheftListener theftListener;

    public MonitorCommand(com.example.ufantigrief.TheftListener theftListener) {
        this.theftListener = theftListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 2) {
            String action = args[0];
            Player target = sender.getServer().getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage("Игрок не найден.");
                return true;
            }

            if (action.equalsIgnoreCase("add")) {
                theftListener.addToWhitelist(target);
                sender.sendMessage(ChatColor.RED + "[ Защита ]" + ChatColor.WHITE + " Игрок " + ChatColor.LIGHT_PURPLE +target.getName() + ChatColor.WHITE +" добавлен в вайт-лист.");
            } else if (action.equalsIgnoreCase("remove")) {
                theftListener.removeFromWhitelist(target);
                sender.sendMessage(ChatColor.RED + "[ Защита ]" + ChatColor.WHITE + " Игрок " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.WHITE +" удален из вайт-листа.");
            } else {
                return false;
            }

            return true;
        } else if (args.length == 1) {
            String action = args[0].toLowerCase();

            if (action.equals("on")) {
                theftListener.enableAlerts(player);
                return true;
            } else if (action.equals("off")) {
                theftListener.disableAlerts(player);
                return true;
            } else {
                player.sendMessage("Некорректная команда. Используйте: /agalerts <on/off>");
                return true;
            }
        } else {
            player.sendMessage("Используйте: /agalerts <on/off> или /aglist <add/remove> <player>");
            return true;
        }
    }
}

