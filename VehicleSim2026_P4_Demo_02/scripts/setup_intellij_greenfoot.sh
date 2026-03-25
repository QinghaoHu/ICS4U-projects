#!/usr/bin/env bash

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_NAME="$(basename "$PROJECT_DIR")"
JDK_VERSION="${1:-17}"

case "$JDK_VERSION" in
  17|21|25)
    ;;
  *)
    echo "Unsupported JDK version: $JDK_VERSION" >&2
    echo "Use one of: 17, 21, 25" >&2
    exit 1
    ;;
esac

LANGUAGE_LEVEL="JDK_${JDK_VERSION}"
IDEA_DIR="$PROJECT_DIR/.idea"
LIB_DIR="$IDEA_DIR/libraries"
MODULE_FILE="$PROJECT_DIR/$PROJECT_NAME.iml"

GREENFOOT_JAR="$PROJECT_DIR/lib/greenfoot.jar"
BLUEJ_JAR="$PROJECT_DIR/lib/bluej.jar"

if [[ ! -f "$GREENFOOT_JAR" ]]; then
  echo "Missing $GREENFOOT_JAR" >&2
  exit 1
fi

if [[ ! -f "$BLUEJ_JAR" ]]; then
  echo "Missing $BLUEJ_JAR" >&2
  exit 1
fi

mkdir -p "$LIB_DIR"

cat > "$IDEA_DIR/compiler.xml" <<'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="CompilerConfiguration">
    <option name="CLEAR_OUTPUT_DIRECTORY" value="false" />
  </component>
</project>
EOF

cat > "$IDEA_DIR/misc.xml" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectRootManager" version="2" languageLevel="$LANGUAGE_LEVEL" default="true" project-jdk-name="$JDK_VERSION" project-jdk-type="JavaSDK">
    <output url="file://\$PROJECT_DIR\$/out" />
  </component>
</project>
EOF

cat > "$IDEA_DIR/modules.xml" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://\$PROJECT_DIR\$/$PROJECT_NAME.iml" filepath="\$PROJECT_DIR\$/$PROJECT_NAME.iml" />
    </modules>
  </component>
</project>
EOF

cat > "$LIB_DIR/greenfoot.xml" <<'EOF'
<component name="libraryTable">
  <library name="greenfoot">
    <CLASSES>
      <root url="jar://$PROJECT_DIR$/lib/greenfoot.jar!/" />
    </CLASSES>
    <JAVADOC />
    <SOURCES />
  </library>
</component>
EOF

cat > "$LIB_DIR/bluej.xml" <<'EOF'
<component name="libraryTable">
  <library name="bluej">
    <CLASSES>
      <root url="jar://$PROJECT_DIR$/lib/bluej.jar!/" />
    </CLASSES>
    <JAVADOC />
    <SOURCES />
  </library>
</component>
EOF

cat > "$MODULE_FILE" <<'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="false">
    <output url="file://$MODULE_DIR$" />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="library" name="bluej" level="project" />
    <orderEntry type="library" name="greenfoot" level="project" />
  </component>
</module>
EOF

chmod +x "$PROJECT_DIR/scripts/compile_greenfoot.sh"

cat <<EOF
Prepared IntelliJ IDEA files for Greenfoot project:
- Project: $PROJECT_NAME
- JDK: $JDK_VERSION
- Module file: $MODULE_FILE

Next steps in IntelliJ IDEA:
1. Reopen or reload the project.
2. If IntelliJ says SDK "$JDK_VERSION" is missing, add that local JDK in Project Structure.
3. Run scripts/compile_greenfoot.sh when you want Greenfoot-style in-place compilation.
EOF
