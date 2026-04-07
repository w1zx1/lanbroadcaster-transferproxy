package net.w1zx1.lanbroadcaster;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@FunctionalInterface
interface MOTDProvider {

    CompletableFuture<String> provideMOTD(Executor executor);
}

