package org.lintx.plugins.yinwuchat.bungee;


import io.netty.channel.Channel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.lintx.plugins.yinwuchat.Util.MessageUtil;
import org.lintx.plugins.yinwuchat.bungee.config.Config;
import org.lintx.plugins.yinwuchat.bungee.httpserver.NettyChannelMessageHelper;
import org.lintx.plugins.yinwuchat.bungee.json.OutputServerMessage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShieldedManage {
    private static final ShieldedManage instance = new ShieldedManage();
    private final Map<String, Man> users = new HashMap<>();

    public static ShieldedManage getInstance() {
        return instance;
    }

    private String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    Result checkShielded(Channel channel, String uuid, String message) {
        Result result = checkShielded(uuid, message);
        if (result.kick) {
            NettyChannelMessageHelper.send(channel, OutputServerMessage.errorJSON(formatMessage(Config.getInstance().tipsConfig.shieldedKickTip)).getJSON());
            channel.close();
        } else if (result.shielded) {
            NettyChannelMessageHelper.send(channel, OutputServerMessage.errorJSON(formatMessage(Config.getInstance().tipsConfig.shieldedTip)).getJSON());
        }
        return result;
    }

    Result checkShielded(ProxiedPlayer player,String message){
        Result result = checkShielded(player.getUniqueId().toString(),message);
        if (result.kick){
            player.disconnect(MessageUtil.newTextComponent(formatMessage(Config.getInstance().tipsConfig.shieldedKickTip)));
        }
        else if (result.shielded){
            player.sendMessage(MessageUtil.newTextComponent(formatMessage(Config.getInstance().tipsConfig.shieldedTip)));
        }
        return result;
    }

    private Result checkShielded(String uuid, String message) {
        String string = message.replaceAll("&([0-9a-fklmnor])", "").replaceAll(" ", "").toLowerCase(Locale.ROOT);
        Result result = new Result();
        Config config = Config.getInstance();
        if (config.shieldeds.parallelStream().anyMatch(string::contains)) {
            YinwuChat.getPlugin().getLogger().info(uuid + " send a shielded word: " + message);
            result.shielded = true;
            if (!users.containsKey(uuid)) {
                Man man = new Man();
                man.first = LocalDateTime.now();
                users.put(uuid, man);
            }

            Man man = users.get(uuid);
            Duration duration = Duration.between(man.first, LocalDateTime.now());
            if (duration.toMillis() > config.shieldedKickTime * 1000) {
                man.first = LocalDateTime.now();
                man.count = 0;
            }
            man.count += 1;
            users.put(uuid, man);
            if (man.count >= config.shieldedKickCount) {
                result.kick = true;
                return result;
            }

            if (config.shieldedMode == 1) {
                result.msg = config.tipsConfig.shieldedReplace;
            } else {
                result.end = true;
            }
        }
        return result;
    }

    private static class Man {
        int count = 0;
        LocalDateTime first = null;
    }

    static class Result {
        boolean shielded = false;
        boolean end = false;
        boolean kick = false;
        String msg = "";
    }
}