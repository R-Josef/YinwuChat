package org.lintx.plugins.yinwuchat.bungee;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.util.UUID;

public class PermissionUtil {

    public static boolean hasPermission(UUID uuid, String permission){
        if (YinwuChat.getPlugin().getProxy().getPluginManager().getPlugin("LuckPerms") == null){
            return true;
        }
        LuckPerms luckPerms = LuckPermsProvider.get();
        return luckPerms.getUserManager().getUser(uuid).getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}
