package me.conorthedev.mediamod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerMessager {

    public static final PlayerMessager INSTANCE = new PlayerMessager();
    private static final ConcurrentLinkedQueue<String> queuedMessages = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<IChatComponent> messages = new ConcurrentLinkedQueue<>();

    private PlayerMessager() {
        TickScheduler.INSTANCE.schedule(0, this::check);
    }

    public void queue(String chat) {
        queuedMessages.add(chat);
    }

    private void check() {
        if (!queuedMessages.isEmpty()) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if (player != null && queuedMessages.peek() != null) {
                String poll = queuedMessages.poll();
                sendMessage(poll);
            }
        }
    }

    public static void sendMessage(IChatComponent message) {
        if (message == null) message = new ChatComponentText("");
        messages.add(message);
    }

    public static void sendMessage(String message) {
        if (message == null) return;
        sendMessage(ChatColor.translateAlternateColorCodes('&', message), true);
    }

    public static void sendMessage(String message, boolean header) {
        if (message == null) return;
        if (header) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                    "&c[&fMediaMod&c]&r " + message)));
        } else {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message)));
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) {
            return;
        }

        while (!messages.isEmpty()) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(messages.poll());
        }

        while (!queuedMessages.isEmpty()) {
            sendMessage(queuedMessages.poll());
        }
    }
}
