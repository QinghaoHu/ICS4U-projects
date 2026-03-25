#!/usr/bin/env bash

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$PROJECT_DIR"

javac \
  -cp "lib/greenfoot.jar:lib/bluej.jar" \
  ./*.java

echo "Compiled Greenfoot sources in place under $PROJECT_DIR"
