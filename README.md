# TransferProxy LANBroadcaster

`TransferProxy LANBroadcaster` is a standalone plugin for `TransferProxy` that broadcasts the proxy over Minecraft LAN discovery.

It periodically sends the standard LAN advertisement payload to `224.0.2.60:4445` using the proxy bind port from the active `TransferProxy` configuration.

## Build

From this folder:

```powershell
.\gradlew.bat jar
```

This project uses a composite build and delegates to the existing Gradle wrapper from the local `TransferProxy` sources.

## Install

Copy `build/libs/TransferProxy-LANBroadcaster-<version>.jar` into the `TransferProxy` `plugins/` directory.

