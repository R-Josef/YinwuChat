package org.lintx.plugins.yinwuchat.bungee;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PermissionUtil {

    public static boolean hasPermission(UUID uuid, String permission){
        if (YinwuChat.getPlugin().getProxy().getPluginManager().getPlugin("LuckPerms") == null){
            ProxiedPlayer player = YinwuChat.getPlugin().getProxy().getPlayer(uuid);
            return player != null && player.hasPermission(permission);
        }
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        } else {
            return false;
        }
    }
}
