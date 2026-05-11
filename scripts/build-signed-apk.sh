#!/usr/bin/env bash
set -euo pipefail

# Build a SIGNED release APK through Gradle with a runtime password prompt.
# Password is not stored in files.
#
# Usage:
#   ./scripts/build-signed-apk.sh
#
# Optional environment overrides:
#   KEYSTORE_PATH         (default: $HOME/keystores/librelune-release.jks)
#   KEY_ALIAS             (default: librelune)
#   KEY_PASSWORD          (default: same as keystore password)
#   OUTPUT_APK            (default: app/build/outputs/apk/release/app-release.apk)
#   WORKDIR               (default: current working directory)

WORKDIR="${WORKDIR:-$PWD}"
KEYSTORE_PATH="${KEYSTORE_PATH:-$HOME/keystores/librelune-release.jks}"
KEY_ALIAS="${KEY_ALIAS:-librelune}"
OUTPUT_APK="${OUTPUT_APK:-app/build/outputs/apk/release/app-release.apk}"

cd "$WORKDIR"

if [[ ! -f "$KEYSTORE_PATH" ]]; then
  echo "Error: keystore not found at $KEYSTORE_PATH" >&2
  echo "Set KEYSTORE_PATH or create the keystore first." >&2
  exit 1
fi

if [[ -z "${JAVA_HOME:-}" ]]; then
  export JAVA_HOME="$HOME/.java/jdk-21.0.3+9/Contents/Home"
fi
export PATH="$JAVA_HOME/bin:$PATH"

if [[ ! -x "./gradlew" ]]; then
  echo "Error: gradlew not found in $WORKDIR" >&2
  exit 1
fi

printf "Keystore password for %s: " "$KEYSTORE_PATH"
read -r -s KS_PASSWORD
echo
if [[ -z "$KS_PASSWORD" ]]; then
  echo "Error: empty password." >&2
  exit 1
fi

KEY_PASSWORD_VALUE="${KEY_PASSWORD:-$KS_PASSWORD}"

echo "Building SIGNED release APK via Gradle..."
LIBRELUNE_RELEASE_STORE_FILE="$KEYSTORE_PATH" \
LIBRELUNE_RELEASE_STORE_PASSWORD="$KS_PASSWORD" \
LIBRELUNE_RELEASE_KEY_ALIAS="$KEY_ALIAS" \
LIBRELUNE_RELEASE_KEY_PASSWORD="$KEY_PASSWORD_VALUE" \
./gradlew assembleRelease

if [[ ! -f "$OUTPUT_APK" ]]; then
  # Fallback to the most recent release APK when output file naming differs.
  OUTPUT_APK="$(ls -t app/build/outputs/apk/release/*.apk 2>/dev/null | head -n1 || true)"
fi

if [[ -z "$OUTPUT_APK" || ! -f "$OUTPUT_APK" ]]; then
  echo "Error: signed APK not found under app/build/outputs/apk/release" >&2
  exit 1
fi

echo "Done. Signed APK: $WORKDIR/$OUTPUT_APK"
