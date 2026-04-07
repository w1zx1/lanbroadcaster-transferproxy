package net.w1zx1.lanbroadcaster;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

final class LANBroadcaster implements Runnable {

    private final ScheduledExecutorService scheduler;
    private final ExecutorService executor;
    private final DatagramSocket socket;
    private final String port;
    private final MOTDProvider motdProvider;
    private final Logger logger;
    private final AtomicInteger failCount;

    private volatile boolean running;
    private volatile ScheduledFuture<?> future;

    private LANBroadcaster(final int port,
                           final MOTDProvider motdProvider,
                           final Logger logger,
                           final DatagramSocket socket) {
        this.port = Integer.toString(port);
        this.motdProvider = motdProvider;
        this.logger = logger;
        this.socket = socket;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            final Thread thread = new Thread(runnable, "transferproxy-lanbroadcaster-scheduler");
            thread.setDaemon(true);
            return thread;
        });
        this.executor = Executors.newCachedThreadPool(runnable -> {
            final Thread thread = new Thread(runnable, "transferproxy-lanbroadcaster-worker");
            thread.setDaemon(true);
            return thread;
        });
        this.failCount = new AtomicInteger();
        this.running = true;
    }

    static LANBroadcaster initialize(final int port,
                                     final MOTDProvider motdProvider,
                                     final Logger logger) throws IOException {
        final DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(3000);
        logger.info("Broadcasting TransferProxy over LAN on port {}", port);
        return new LANBroadcaster(port, motdProvider, logger, socket);
    }

    void schedule() {
        this.future = this.scheduler.scheduleAtFixedRate(this, 0L, 1500L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        if (!this.running) {
            final ScheduledFuture<?> currentFuture = this.future;
            if (currentFuture != null) {
                currentFuture.cancel(false);
            }
            return;
        }

        this.getAd().thenAcceptAsync(ad -> {
            final DatagramPacket packet = new DatagramPacket(ad, ad.length, Constants.BROADCAST_ADDRESS, Constants.BROADCAST_PORT);
            try {
                this.socket.send(packet);
            } catch (final IOException exception) {
                throw new UncheckedIOException(exception);
            }
            this.failCount.set(0);
        }, this.executor).whenCompleteAsync((unused, throwable) -> {
            if (throwable != null) {
                this.fail(throwable);
            }
        }, this.executor);
    }

    void shutdown() {
        this.running = false;
        final ScheduledFuture<?> currentFuture = this.future;
        if (currentFuture != null) {
            currentFuture.cancel(true);
        }
        this.scheduler.shutdownNow();
        this.executor.shutdownNow();
        this.socket.close();
    }

    private void fail(final Throwable throwable) {
        final int failures = this.failCount.getAndIncrement();
        if (failures == 0) {
            this.logger.warn("Failed to broadcast LAN advertisement.", throwable);
        }

        final int currentFailures = this.failCount.get();
        if (currentFailures < 5) {
            this.logger.warn("Failed to broadcast LAN advertisement. Trying again in 10 seconds...");
        } else if (currentFailures == 5) {
            this.logger.error("LAN broadcasting will stay paused until the network is fixed. Further warnings are muted.");
        }

        final ScheduledFuture<?> currentFuture = this.future;
        if (currentFuture != null) {
            currentFuture.cancel(true);
        }

        this.scheduler.schedule(this::schedule, 8500L, TimeUnit.MILLISECONDS);
    }

    private CompletableFuture<byte[]> getAd() {
        return this.motdProvider.provideMOTD(this.executor)
                .thenApply(motd -> "[MOTD]%s[/MOTD][AD]%s[/AD]"
                        .formatted(motd == null ? "" : motd.replace('\n', ' '), this.port)
                        .getBytes(StandardCharsets.UTF_8));
    }
}

