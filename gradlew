#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a symlink
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls -ld "$PRG"
    link=`expr "$PRG" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MSYS* | MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar


# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$nonstop" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# For Darwin, add options to specify how the application appears in the dock
if [ "$darwin" = "true" ] ; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

# For Cygwin or MSYS, switch paths to Windows format before running java
if [ "$cygwin" = "true" -o "$msys" = "true" ] ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`

    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 2 -name .gradle -o -prune -o -type f -name gradle.jar | tr '\n' ':'`
    ROOTDIRS=`cygpath --path --ignore --mixed "$ROOTDIRSRAW" | cut -d: -f1-19`
    # Adding a '/' prefix keeps the next argument from being interpreted as a flag.
    #    set -- "$@" "$ROOTDIRS"
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`

    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 2 -name .gradle -o -prune -o -type f -name gradle.jar | tr '\n' ':'`
    ROOTDIRS=`cygpath --path --ignore --mixed "$ROOTDIRSRAW" | cut -d: -f1-19`
fi

MAVEN_PROJECTBASEDIR="`cd "$INPUT_DIR"; pwd`"
export MAVEN_PROJECTBASEDIR
if [ "$CYGWIN" = "true" ]; then
  export PYTHONPATH="`cygpath --path --mixed $PYTHONPATH`"
fi

# Opting out of JDK 16's strong encapsulation is an accident waiting to happen.
# We are intentionally vague about the version here in hopes that we remain compatible
# for several releases of Java.
JAVA_TOOLCHAIN_VERSION=16
if type -p java > /dev/null; then
    JAVA_VERSION=$("$JAVACMD" -version 2>&1 | grep -oP 'version "\K[^"]*' | cut -d. -f1)
    if [ "$JAVA_VERSION" -ge "$JAVA_TOOLCHAIN_VERSION" ] ; then
        JVM_OPTS="$JVM_OPTS --add-modules jdk.incubator.vector"
    fi
fi

# Escape application args
save () {
    for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
    echo " "
}
APP_ARGS=`save "$@"`

# Collect all arguments for the java command, stacking in reverse order:
#   * DEFAULT_JVM_OPTS
#   * JAVA_OPTS
#   * GRADLE_OPTS
#   * Parameters passed to gradle

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 2 -name .gradle -o -prune -o -type f -name gradle.jar | tr '\n' ':'`
    ROOTDIRS=`cygpath --path --ignore --mixed "$ROOTDIRSRAW" | cut -d: -f1-19`

    # Adding a '/' prefix keeps the next argument from being interpreted as a flag.
    #    set -- "$@" "$ROOTDIRS"
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`

    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 2 -name .gradle -o -prune -o -type f -name gradle.jar | tr '\n' ':'`
    ROOTDIRS=`cygpath --path --ignore --mixed "$ROOTDIRSRAW" | cut -d: -f1-19`

    # Adding a '/' prefix keeps the next argument from being interpreted as a flag.
    #    set -- "$@" "$ROOTDIRS"
fi

case $UNAME in
    MINGW*)
        # Use Windows-style path
        ;;
    *)
        # Use Unix-style path
        ;;
esac

# For MINGW or MSYS, switch paths to Windows format before running java
if expr "$OSTYPE" : 'msys\|mingw' > /dev/null; then
    APP_HOME=`( cd "$APP_HOME" && pwd -W )`
    CLASSPATH=`( cd "$( dirname "$CLASSPATH" )" && pwd -W )`/gradle-wrapper.jar
    JAVACMD=`( cd "$( dirname "$JAVACMD" )" && pwd -W )`/java
    [ -n "$JAVA_HOME" ] && JAVA_HOME=`( cd "$JAVA_HOME" && pwd -W )`
fi

$JAVACMD $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
