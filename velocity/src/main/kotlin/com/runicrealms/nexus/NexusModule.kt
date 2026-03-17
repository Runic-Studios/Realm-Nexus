package com.runicrealms.nexus

import com.google.inject.AbstractModule
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger

class NexusModule(
    private val plugin: NexusPlugin,
    private val proxy: ProxyServer,
    private val logger: Logger,
) : AbstractModule() {

    override fun configure() {
        bind(NexusPlugin::class.java).toInstance(plugin)
        bind(ProxyServer::class.java).toInstance(proxy)
        bind(Logger::class.java).toInstance(logger)
    }
}
