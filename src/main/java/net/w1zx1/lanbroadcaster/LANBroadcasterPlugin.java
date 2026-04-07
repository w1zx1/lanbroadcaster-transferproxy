package net.w1zx1.lanbroadcaster;

import net.transferproxy.api.TransferProxy;
import net.transferproxy.api.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LANBroadcasterPlugin implements Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(LANBroadcasterPlugin.class);

    private LANBroadcaster broadcaster;

    @Override
    public void onEnable() {
        final int port = TransferProxy.getInstance().getConfiguration().getNetwork().getBindPort();
        try {
            this.broadcaster = LANBroadcaster.initialize(port, new TransferProxyMOTDProvider(), LOGGER);
            this.broadcaster.schedule();
            LOGGER.info("TransferProxy LAN broadcaster is enabled.");
        } catch (final Exception exception) {
            LOGGER.error("TransferProxy LAN broadcaster could not be initialized.", exception);
        }
    }

    @Override
    public void onDisable() {
        if (this.broadcaster != null) {
            this.broadcaster.shutdown();
            this.broadcaster = null;
        }
        LOGGER.info("TransferProxy LAN broadcaster is disabled.");
    }
}

