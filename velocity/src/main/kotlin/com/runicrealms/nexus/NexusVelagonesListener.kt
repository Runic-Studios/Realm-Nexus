package com.runicrealms.nexus

import com.google.inject.Inject
import com.runicrealms.velagones.velocity.api.event.server.VelagonesDeactivateServerEvent
import com.runicrealms.velagones.velocity.api.event.server.VelagonesDiscoverServerEvent
import com.runicrealms.velagones.velocity.api.event.server.VelagonesRemoveServerEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/** Notifies the network when Velagones adds/removes/deactivates servers */
class NexusVelagonesListener
@Inject
constructor(private val plugin: NexusPlugin, private val proxy: ProxyServer) {

    init {
        proxy.eventManager.register(plugin, this)
    }

    private fun broadcast(message: Component) {
        for (player in proxy.allPlayers) {
            player.sendMessage(message)
        }
    }

    @Subscribe
    fun onVelagonesDiscover(event: VelagonesDiscoverServerEvent) {
        val name = event.server.registeredServer.serverInfo.name
        val group = event.server.group
        broadcast(
            Component.text("[Nexus]", NamedTextColor.DARK_PURPLE)
                .append(Component.text(" >> ", NamedTextColor.GOLD))
                .append(Component.text("Server discovered: ", NamedTextColor.LIGHT_PURPLE))
                .append(Component.text("$name [$group]", NamedTextColor.WHITE))
        )
    }

    @Subscribe
    fun onVelagonesDeactivate(event: VelagonesDeactivateServerEvent) {
        val name = event.server.registeredServer.serverInfo.name
        val group = event.server.group
        broadcast(
            Component.text("[Nexus]", NamedTextColor.DARK_PURPLE)
                .append(Component.text(" >> ", NamedTextColor.GOLD))
                .append(Component.text("Server draining: ", NamedTextColor.YELLOW))
                .append(Component.text("$name [$group]", NamedTextColor.WHITE))
        )
    }

    @Subscribe
    fun onVelagonesRemove(event: VelagonesRemoveServerEvent) {
        val name = event.server.registeredServer.serverInfo.name
        val group = event.server.group
        broadcast(
            Component.text("[Nexus]", NamedTextColor.DARK_PURPLE)
                .append(Component.text(" >> ", NamedTextColor.GOLD))
                .append(Component.text("Server removed: ", NamedTextColor.RED))
                .append(Component.text("$name [$group]", NamedTextColor.WHITE))
        )
    }
}
