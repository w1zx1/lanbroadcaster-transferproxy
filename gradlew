#!/bin/sh
SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
exec "$SCRIPT_DIR/transferproxy/gradlew" -p "$SCRIPT_DIR" "$@"
