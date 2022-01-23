package net.stackdoubleflow.wakatimemc

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient

object WakaTime : ClientModInitializer {
    private var lastSentTime = 0L
    private lateinit var lastServer: String

    override fun onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register {
            val client = MinecraftClient.getInstance()
            val currentServer = client.currentServerEntry
            if (currentServer != null) {
                println(currentServer.name)
            }
        }
    }
}