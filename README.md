# LANBroadcaster (для TransferProxy)

порт [4drian3d/LANBroadcaster](https://github.com/4drian3d/LANBroadcaster) для [TransferProxy](https://github.com/YvanMazy/TransferProxy)

плагин периодически отправляет стандартный lan-пакет на `224.0.2.60:4445` и использует порт из текущей конфигурации `TransferProxy`

## сборка

### зависимости

- java 17

```
.\gradlew.bat clean shadowJar
```

проект использует composite build и вызывает локальный gradle wrapper из исходников `transferproxy`

## установка

скопировать `build/libs/LANBroadcaster-<version>.jar` в папку `plugins/`

## примечания

RGB-цвета и градиенты из MiniMessage могут отображаться упрощенно
