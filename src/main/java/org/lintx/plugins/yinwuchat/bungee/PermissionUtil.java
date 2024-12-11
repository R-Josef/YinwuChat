package org.lintx.plugins.yinwuchat.bungee;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.UUID;

public class PermissionUtil {

    public static boolean hasPermission(UUID uuid, String permission){
        if (YinwuChat.getPlugin().getProxy().getPluginManager().getPlugin("LuckPerms") == null){
            return true;
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
