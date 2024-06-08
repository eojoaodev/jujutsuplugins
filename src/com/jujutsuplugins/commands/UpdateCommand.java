package com.jujutsuplugins.commands;

import com.jujutsuplugins.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpdateCommand implements CommandExecutor {

    private final Main plugin;

    public UpdateCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("update")) {
            sender.sendMessage("§cUso correto: /jujutsu update");
            return true;
        }

        if (plugin.isUpdateAvailable()) {
            sender.sendMessage("§eUma nova atualização está disponível! Verifique o GitHub para detalhes.");
        } else {
            sender.sendMessage("§aSeu plugin está atualizado.");
        }

        return true;
    }
}