package net.w1zx1.lanbroadcaster;

import java.net.InetAddress;
import java.net.UnknownHostException;

final class Constants {

    static final InetAddress BROADCAST_ADDRESS;
    static final int BROADCAST_PORT = 4445;

    static {
        try {
            BROADCAST_ADDRESS = InetAddress.getByName("224.0.2.60");
        } catch (final UnknownHostException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private Constants() {
    }
}

