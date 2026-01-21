#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "$(realpath "$0")")/.."

if ! git diff --quiet; then
    echo >&2 "Dirty workspace! Commit any changes and try again..."
    exit 1
fi

current_version="$(grep -oP '^\s*mod_version\s*=\s*\K[0-9]+\.[0-9]+\.[0-9]+\s*$' gradle.properties)"

case "${1:-"none"}" in
    "major")
        next_version="$(echo "${current_version}" | awk -F '.' '{$1+=1; OFS="."; print}' | tr ' ' '.')"
        ;;
    "minor")
        next_version="$(echo "${current_version}" | awk -F '.' '{$2+=1; OFS="."; print}' | tr ' ' '.')"
        ;;
    "patch")
        next_version="$(echo "${current_version}" | awk -F '.' '{$3+=1; OFS="."; print}' | tr ' ' '.')"
        ;;
    *)
        echo >&2 "Usage: ./$0 (major|minor|patch)"
        exit 1
        ;;
esac

next_tag="v${next_version}"

sed -i -E "s/^\s*(mod_version\s*=\s*)[0-9]+\.[0-9]+\.[0-9]+/\1${next_version}/" gradle.properties
git commit -m "${next_tag}" gradle.properties

git log "origin/$(git branch --show-current)..HEAD"
git push

git tag -s "${next_tag}" -m "Release ${next_tag}"
git push origin "${next_tag}"
