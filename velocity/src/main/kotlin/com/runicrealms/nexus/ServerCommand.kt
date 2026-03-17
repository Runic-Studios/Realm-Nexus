package com.runicrealms.nexus

import com.runicrealms.velagones.velocity.api.VelagonesAPI
import com.runicrealms.velagones.velocity.api.event.VelagonesInitializeEvent
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

class ServerCommand : SimpleCommand {

    private lateinit var velagonesAPI: VelagonesAPI

    @Subscribe
    fun onVelagonesInitialization(event: VelagonesInitializeEvent) {
        velagonesAPI = event.velagonesAPI
    }

    override fun execute(invocation: SimpleCommand.Invocation) {
        val source = invocation.source()
        val args = invocation.arguments()
        if (args.isEmpty()) {
            source.sendMessage(
                Component.text("[Nexus]", NamedTextColor.DARK_PURPLE)
                    .append(Component.text(" >> ", NamedTextColor.GOLD))
                    .append(Component.text("Available Servers", NamedTextColor.LIGHT_PURPLE))
            )
            val serversByGroup = velagonesAPI.getGameServers().groupBy { it.group }.toSortedMap()
            if (serversByGroup.isEmpty()) {
                source.sendMessage(
                    Component.text("No servers are currently available.", NamedTextColor.GRAY)
                )
            } else {
                for ((group, servers) in serversByGroup) {
                    source.sendMessage(
                        Component.text("  ", NamedTextColor.DARK_GRAY)
                            .append(Component.text(group, NamedTextColor.YELLOW))
                    )
                    for (server in servers.sortedBy { it.registeredServer.serverInfo.name }) {
                        val name = server.registeredServer.serverInfo.name
                        val players = server.registeredServer.playersConnected.size
                        val capacity = server.capacity
                        val status =
                            if (server.deactivated) {
                                Component.text(" (draining)", NamedTextColor.DARK_GRAY)
                            } else {
                                Component.empty()
                            }
                        source.sendMessage(
                            Component.text("    ", NamedTextColor.DARK_GRAY)
                                .append(Component.text(name, NamedTextColor.WHITE))
                                .append(Component.text(": ", NamedTextColor.GRAY))
                                .append(Component.text("$players/$capacity", NamedTextColor.AQUA))
                                .append(status)
                        )
                    }
                }
            }
            source.sendMessage(
                Component.text("Use ", NamedTextColor.GRAY)
                    .append(Component.text("/server <name>", NamedTextColor.WHITE))
                    .append(Component.text(" to connect.", NamedTextColor.GRAY))
            )
            return
        }
        if (source !is Player) {
            source.sendMessage(
                Component.text("You must be a player to connect to a server.", NamedTextColor.RED)
            )
            return
        }
        val targetName = args[0]!!
        val target =
            velagonesAPI.getGameServers().firstOrNull {
                it.registeredServer.serverInfo.name.equals(targetName, ignoreCase = true)
            }
        if (target == null) {
            source.sendMessage(
                Component.text("Unknown server: ", NamedTextColor.RED)
                    .append(Component.text(targetName, NamedTextColor.WHITE))
                    .append(
                        Component.text(
                            ". Use /server with no arguments to list available servers.",
                            NamedTextColor.GRAY,
                        )
                    )
            )
            return
        }
        if (target.deactivated && !source.hasPermission("runic.op")) {
            source.sendMessage(
                Component.text("That server is draining. ", NamedTextColor.RED)
                    .append(
                        Component.text(
                            "Only operators can connect to draining servers.",
                            NamedTextColor.GRAY,
                        )
                    )
            )
            return
        }
        source.sendMessage(
            Component.text("[Nexus]", NamedTextColor.DARK_PURPLE)
                .append(Component.text(" >> ", NamedTextColor.GOLD))
                .append(Component.text("Sending you to ", NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(targetName, NamedTextColor.WHITE))
        )
        val result = source.createConnectionRequest(target.registeredServer).connect().get()
        if (!result.isSuccessful) {
            source.sendMessage(
                Component.text("Could not connect to ", NamedTextColor.RED)
                    .append(Component.text(targetName, NamedTextColor.WHITE))
                    .append(
                        Component.text(
                            ". The server may be full or unavailable.",
                            NamedTextColor.GRAY,
                        )
                    )
            )
        }
    }
}
