package net.stackdoubleflow.wakatimemc

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Vec3d

private const val HEARTBEAT_INTERVAL = 120000000000 // 2 Minutes

object WakaTime : ClientModInitializer {
    private var lastSentTime = 0L
    private var lastServer: String? = null

    var keyPressed = false

    // Editor: Minecraft 1.17.1
    // Language: Minecraft
    // Project: <server address>
    // Filename: <server address>

    fun sendHeartbeat(isWrite: Boolean) {
        val client = MinecraftClient.getInstance()
        val currentServer = client.currentServerEntry ?: return
        val now = System.nanoTime()
        if (!isWrite && lastServer != currentServer.address && (now - lastSentTime) < HEARTBEAT_INTERVAL) {
            return
        }

        val home = System.getProperty("user.home")
        var command = mutableListOf(
            "${home}/.wakatime/wakatime-cli/wakatime-cli",
            "--entity-type", "domain",
            "--entity", currentServer.address,
            "--project", currentServer.address,
            "--language", "Minecraft",
            "--plugin", "\"Minecraft/1.17.1 WakaTimeMC/1.0.0\""
        )
        if (isWrite) {
            command.add("--write")
        }
        val processBuilder = ProcessBuilder(command)
        processBuilder.inheritIO()
        processBuilder.start()

        lastServer = currentServer.address
        lastSentTime = now
    }

    override fun onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register {
            if (keyPressed) {
                sendHeartbeat(false)
                keyPressed = false
            }
        }

        PlayerBlockBreakEvents.AFTER.register { _, _, _, _, _ ->
            sendHeartbeat(true)
        }

        UseBlockCallback.EVENT.register { _, _, _, _ ->
            sendHeartbeat(true)
            ActionResult.PASS
        }
    }
}