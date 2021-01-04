package org.lintx.plugins.yinwuchat.bukkit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.lintx.plugins.yinwuchat.Const;
import org.lintx.plugins.yinwuchat.Util.GsonUtil;
import org.lintx.plugins.yinwuchat.Util.ItemUtil;
import org.lintx.plugins.yinwuchat.Util.MessageUtil;
import org.lintx.plugins.yinwuchat.json.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageManage {
    private static final MessageManage instance = new MessageManage();
    private YinwuChat plugin;

    private MessageManage() {
    }

    public static MessageManage getInstance() {
        return instance;
    }

    void setPlugin(YinwuChat plugin) {
        this.plugin = plugin;
    }

    private String filterStyle(Player player,String chat){
        String permissions = "0123456789abcdefklmnor";
        StringBuilder deny = new StringBuilder();
        for (int i=0;i<permissions.length();i++){
            String p = permissions.substring(i, i+1);
            String permission = "yinwuchat.style." + p;
            if (!player.hasPermission(permission)){
                deny.append(p);
            }
        }
        if (!"".equals(deny.toString())){
            chat = MessageUtil.filter(chat, deny.toString());
        }
        if (!player.hasPermission("yinwuchat.style.rgb")){
            chat = MessageUtil.filterRGB(chat);
        }
        return chat;
    }

    public void onPrivateMessage(Player player, String toPlayerName, String chat) {
        chat = filterStyle(player, chat);
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.toPlayer = toPlayerName;
        privateMessage.player = player.getName();
        privateMessage.chat = chat;

        privateMessage.toFormat = format(player, Config.getInstance().toFormat);
        privateMessage.fromFormat = format(player, Config.getInstance().fromFormat);

        for (HandleConfig config : Config.getInstance().messageHandles) {
            HandleConfig handleConfig = new HandleConfig();
            handleConfig.placeholder = config.placeholder;
            handleConfig.format = format(player, config.format);
            privateMessage.handles.add(handleConfig);
        }

        privateMessage.items = getMessageItems(chat, player);
        sendPluginMessage(player, Const.PLUGIN_SUB_CHANNEL_PRIVATE_MESSAGE, privateMessage);
    }

    void onPlayerDeath(Player player, String death) {
        Message msg = new Message();
        msg.player = player.getName();
        msg.chat = death;
        msg.items = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).map(String::valueOf).collect(Collectors.toCollection(LinkedList::new));
        sendPluginMessage(player, Const.PLUGIN_SUB_CHANNEL_PLAYER_DEATH, msg);
    }

    void onPublicMessage(Player player, String chat) {
        chat = filterStyle(player, chat);
        PublicMessage publicMessage = new PublicMessage();
        publicMessage.player = player.getDisplayName();
        publicMessage.chat = chat;

        publicMessage.format = format(player, Config.getInstance().format);

        for (HandleConfig config : Config.getInstance().messageHandles) {
            HandleConfig handleConfig = new HandleConfig();
            handleConfig.placeholder = config.placeholder;
            handleConfig.format = format(player, config.format);
            publicMessage.handles.add(handleConfig);
        }

        publicMessage.items = getMessageItems(chat, player);

        sendPluginMessage(player, Const.PLUGIN_SUB_CHANNEL_PUBLIC_MESSAGE, publicMessage);
    }

    private void sendPluginMessage(Player player, String channel, Message message) {
        String json = GsonUtil.GSON.toJson(message);
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(channel);
        output.writeUTF(json);
        player.sendPluginMessage(plugin, Const.PLUGIN_CHANNEL, output.toByteArray());
    }

    private List<MessageFormat> format(Player player, List<MessageFormat> formats) {
        boolean hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null &&
                Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        List<MessageFormat> list = new ArrayList<>();
        for (MessageFormat message : formats) {
            if (message.message == null || message.message.equals("")) continue;

            String msg = message.message;
            msg = papi(hasPAPI, msg, player);

            MessageFormat format = new MessageFormat(msg);
            if (message.hover != null) {
                format.hover = papi(hasPAPI, message.hover, player);
            }
            if (message.click != null) {
                format.click = papi(hasPAPI, message.click, player);
            }
            list.add(format);
        }
        return list;
    }

    private String papi(boolean open, String string, Player player) {
        if (!open) {
            return string;
        }
        try {
            return PlaceholderAPI.setPlaceholders(player, string);
        } catch (Exception ignored) {
            return string;
        }
    }

    private List<String> getMessageItems(String message, Player player) {
        Pattern pattern = Pattern.compile(Const.ITEM_PLACEHOLDER);
        Matcher matcher = pattern.matcher(message);
        List<String> list = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
        while (matcher.find()) {
            int index = -1;
            String s = matcher.group(2);
            try {
                index = Integer.parseInt(s);
                if (index > 40 || index < 0) {
                    index = -1;
                }
            } catch (Exception ignored) {
            }
            ItemStack itemStack;
            if (index == -1) {
                itemStack = inventory.getItemInMainHand().getType() == Material.AIR
                        ? player.getInventory().getItemInOffHand()
                        : player.getInventory().getItemInMainHand();
            } else {
                itemStack = inventory.getItem(index);
            }

            list.add(ItemUtil.itemJsonWithPlayer(itemStack));
        }
        return list;
    }
}