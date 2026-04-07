package net.w1zx1.lanbroadcaster;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.transferproxy.api.TransferProxy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

final class TransferProxyMOTDProvider implements MOTDProvider {

    @Override
    public CompletableFuture<String> provideMOTD(final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            final String configuredDescription = TransferProxy.getInstance()
                    .getConfiguration()
                    .getStatus()
                    .getDescription();
            return MiniMessage.miniMessage().stripTags(configuredDescription);
        }, executor);
    }
}

