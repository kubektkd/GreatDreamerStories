#!/bin/bash

set -euo pipefail

GAME_NAME="GreatDreamerStories"
VENDOR="AtomicCode"
MAIN_JAR="${GAME_NAME}.jar"
INPUT_DIR="build/jpackage-input"
OUTPUT_DIR="dist"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

usage() {
    cat << EOF
Great Dreamer Stories — native app-image builder

Version is read from repo root gradle.properties (key: version).

Usage:
  $(basename "$0") [options]

Options:
  --patch               Bump PATCH (z in x.y.z), then package
  --minor               Bump MINOR, reset PATCH to 0, then package
  --major               Bump MAJOR, reset MINOR and PATCH, then package
  --no-version-bump    Package using current gradle.properties version (no bump)
  -h, --help            Show this help

If no bump option is passed and stdin is a TTY, you will be prompted interactively.
In non-interactive environments, pass one of --patch, --minor, --major, or --no-version-bump.

Examples:
  $(basename "$0") --patch
  $(basename "$0") --no-version-bump
EOF
}

read_version_from_props() {
    local props="${REPO_ROOT}/gradle.properties"
    local line
    while IFS= read -r line || [ -n "$line" ]; do
        [[ "$line" =~ ^[[:space:]]*# ]] && continue
        [[ "$line" =~ ^[[:space:]]*version[[:space:]]*= ]] || continue
        local val="${line#*=}"
        val="${val#"${val%%[![:space:]]*}"}"
        val="${val%"${val##*[![:space:]]}"}"
        printf '%s\n' "${val}"
        return 0
    done < "${props}"
    echo "Error: no 'version=' line found in ${props}" >&2
    exit 1
}

write_version_to_props() {
    local new_ver="$1"
    local props="${REPO_ROOT}/gradle.properties"
    local tmp
    tmp="$(mktemp)"
    awk -v nv="$new_ver" '
      BEGIN { found = 0 }
      /^[[:space:]]*#/ { print; next }
      /^[[:space:]]*version[[:space:]]*=/ { print "version=" nv; found = 1; next }
      { print }
      END {
        if (!found) {
          print "version=" nv
        }
      }
    ' "${props}" > "${tmp}"
    mv "${tmp}" "${props}"
}

bump_semver() {
    local cur="$1" kind="$2"
    if [[ ! "$cur" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
        echo "Error: version must be semver x.y.z (digits only), got: ${cur}"
        exit 1
    fi
    local major="${BASH_REMATCH[1]}"
    local minor="${BASH_REMATCH[2]}"
    local patch="${BASH_REMATCH[3]}"
    case "$kind" in
        patch) patch=$((patch + 1)) ;;
        minor) minor=$((minor + 1)); patch=0 ;;
        major) major=$((major + 1)); minor=0; patch=0 ;;
        *) echo "Error: unknown bump kind: $kind"; exit 1 ;;
    esac
    printf '%s.%s.%s\n' "$major" "$minor" "$patch"
}

interactive_bump_choice() {
    local current="$1"
    # UI must go to stderr: stdout is captured by INTERACTIVE="$(...)" at the call site.
    echo "" >&2
    echo "Current version from gradle.properties: ${current}" >&2
    echo "Choose how to set the version for this package:" >&2
    echo "  1) patch  — z+1 (${current} → $(bump_semver "$current" patch))" >&2
    echo "  2) minor  — y+1, z=0 (${current} → $(bump_semver "$current" minor))" >&2
    echo "  3) major  — x+1, y=z=0 (${current} → $(bump_semver "$current" major))" >&2
    echo "  4) none   — keep ${current}" >&2
    echo "" >&2
    read -r -p "Enter 1-4 [default=1]: " choice
    choice="${choice:-1}"
    case "$choice" in
        1) printf '%s\n' "patch" ;;
        2) printf '%s\n' "minor" ;;
        3) printf '%s\n' "major" ;;
        4) printf '%s\n' "none" ;;
        *) echo "Invalid choice." >&2; exit 1 ;;
    esac
}

BUMP_KIND=""
SKIP_BUMP=0

while [ $# -gt 0 ]; do
    case "$1" in
        --patch) BUMP_KIND="patch" ;;
        --minor) BUMP_KIND="minor" ;;
        --major) BUMP_KIND="major" ;;
        --no-version-bump) SKIP_BUMP=1 ;;
        -h|--help) usage; exit 0 ;;
        *)
            echo "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
    shift
done

cd "${REPO_ROOT}"

if [ "$SKIP_BUMP" -eq 1 ] && [ -n "$BUMP_KIND" ]; then
    echo "Error: use either --no-version-bump or one of --patch/--minor/--major, not both."
    exit 1
fi

CURRENT_VER="$(read_version_from_props)"

if [ "$SKIP_BUMP" -eq 0 ]; then
    if [ -z "$BUMP_KIND" ]; then
        if [ -t 0 ]; then
            INTERACTIVE="$(interactive_bump_choice "$CURRENT_VER")"
            if [ "$INTERACTIVE" = "none" ]; then
                SKIP_BUMP=1
            else
                BUMP_KIND="$INTERACTIVE"
            fi
        else
            echo "Error: non-interactive terminal; specify --patch, --minor, --major, or --no-version-bump."
            exit 1
        fi
    fi
fi

if [ "$SKIP_BUMP" -eq 0 ]; then
    NEW_VER="$(bump_semver "$CURRENT_VER" "$BUMP_KIND")"
    write_version_to_props "$NEW_VER"
    VERSION="$(read_version_from_props)"
    echo ""
    echo "Version bumped (${BUMP_KIND}): ${CURRENT_VER} → ${VERSION}"
else
    VERSION="$(read_version_from_props)"
    echo ""
    echo "Packaging with version ${VERSION} (no bump)"
fi

case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) PLATFORM="windows" ;;
    Linux*) PLATFORM="linux" ;;
    Darwin*) PLATFORM="macos" ;;
    *) PLATFORM="unknown" ;;
esac

echo
echo "Great Dreamer Stories distribution builder"
echo "Target platform: ${PLATFORM}"
echo "App version: ${VERSION}"
echo

if ! command -v jpackage >/dev/null 2>&1; then
    echo "Error: jpackage is not available. Use JDK 17 or newer."
    exit 1
fi

./gradlew clean stageJpackage

# App-image folder name under dist/<platform>/ (includes version for quick identification).
APP_IMAGE_DIR_NAME="${GAME_NAME} v.${VERSION}"

rm -rf "${OUTPUT_DIR}/${PLATFORM}"
mkdir -p "${OUTPUT_DIR}/${PLATFORM}"

jpackage \
    --type app-image \
    --name "${GAME_NAME}" \
    --app-version "${VERSION}" \
    --description "${GAME_NAME} - ${VENDOR}" \
    --vendor "${VENDOR}" \
    --main-jar "${MAIN_JAR}" \
    --main-class "atomiccode.greatDreamerStories.Launcher" \
    --input "${INPUT_DIR}" \
    --dest "${OUTPUT_DIR}/${PLATFORM}" \
    --java-options "-Xms512m" \
    --java-options "-Xmx2g" \
    --verbose

mv "${OUTPUT_DIR}/${PLATFORM}/${GAME_NAME}" "${OUTPUT_DIR}/${PLATFORM}/${APP_IMAGE_DIR_NAME}"

echo
echo "Distribution created at ${OUTPUT_DIR}/${PLATFORM}/${APP_IMAGE_DIR_NAME}"
echo "Run this script on Windows, Linux, and macOS to create native app images for each OS."
