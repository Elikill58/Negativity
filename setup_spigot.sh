#!/bin/bash

trap "exit" INT

DIR="spigot/spigotJars/install"
if [ -d "$DIR" ]; then
   echo "$DIR already created" > /dev/null
else
   mkdir -p $DIR
fi
cd $DIR || exit


case "$OSTYPE" in
  solaris*) os=solaris ;;
  darwin*)  os=macos ;;
  linux*)   os=linux ;;
  bsd*)     os=bsd ;;
  msys*)    os=windows ;;
  cygwin*)  os=windows ;;
  *)        os=unknown:$OSTYPE ;;
esac

extension=.tar.gz
executable=

if [[ $os == "windows" ]]; then
   extension=.zip
   executable=.exe
fi
echo "OS used: $os with $extension"

java8=unknown
java16=unknown
java17=unknown

function installJDK() {
   echo "Failed to find Java $1. Downloading it..."
   curl -L -o "Java$1$extension" "$2"
   mkdir "jdk-$1"
   if [[ $extension == ".zip" ]]; then
      unzip "Java$1$extension" -d "jdk-$1"
   else
      tar -xvf "Java$1$extension" -C "jdk-$1" --strip-components 1
   fi
   rm "Java$1$extension"
}

if [[ $java8 == *"unknown"* || $java16 == *"unknown"* || $java17 == *"unknown"* ]]; then # if at least one JAVA is NOT set
   export IFS=";"
   for javaVersionPath in $JAVA_HOME; do
       if [[ $javaVersionPath == *"1.8"* ]]; then # check if has "1.8"
          java8="$javaVersionPath/bin/java$executable"
       elif [[ $javaVersionPath == *"16."* ]]; then # check if has "16."
          java16="$javaVersionPath/java$executable"
       elif [[ $javaVersionPath == *"17."* ]]; then # check if has "17."
          java17="$javaVersionPath/java$executable"
       fi
   done

   if [[ $java8 == *"unknown"* ]]; then
      if [ -d "./jdk-8" ]; then
         echo "Java 8 and already downloaded, using this version..."
      else
         installJDK 8 "https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u345-b01/OpenJDK8U-jdk_x64_${os}_hotspot_8u345b01$extension"
      fi
     java8="$PWD/jdk-8/bin/java$executable"
   fi
   if [[ $java16 == *"unknown"* ]]; then
      if [ -d "./jdk-16" ]; then
         echo "Java 16 and already downloaded, using this version..."
      else
         installJDK 16 "https://download.java.net/openjdk/jdk16/ri/openjdk-16+36_$os-x64_bin$extension"
      fi
     java16="$PWD/jdk-16/bin/java$executable"
   fi
   if [[ $java17 == *"unknown"* ]]; then
      if [ -d "./jdk-17" ]; then
         echo "Java 17 and already downloaded, using this version..."
      else
         installJDK 17 "https://download.oracle.com/java/17/latest/jdk-17_$os-x64_bin$extension"
      fi
     java17="$PWD/jdk-17/bin/java$executable"
   fi
fi

echo "Using java8: $java8"
echo "Using java16: $java16"
echo "Using java17: $java17"

echo "-------- WARN --------"
echo ""
echo ""
echo "This will take some time to complete. Please wait until it's finished."
echo ""
echo ""
echo "-------- WARN --------"

sleep 3s

curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
for mcVersion in "1.8.8" "1.9.2" "1.9.4" "1.10.2" "1.11.2" "1.12" "1.13" "1.13.2" "1.14.4" "1.15" "1.16.1" "1.16.4"; do
   spigotFile=spigot-$mcVersion.jar
   if [ -f "../$spigotFile" ]; then
      echo "Spigot $mcVersion already exists. Skipping..."
   else
      mcCmd="\"$java8\" -jar BuildTools.jar --rev $mcVersion"
      echo "Running $mcCmd ..."
      eval "$mcCmd"
      if [ -f "$spigotFile" ]; then # if build successful
         cp $spigotFile "../$spigotFile"
      else
         echo "Failed to build version $mcVersion."
         exit 1
      fi
   fi
done
if [ ! -f "../spigot-0-1.13.jar" ]; then
   cp "../spigot-1.13.jar" "../spigot-0-1.13.jar" # the first jar of the list
fi
for mcVersion in "1.17" "1.18" "1.18.2" "1.19"; do # all java17 remapped versions
   snap="$mcVersion-R0.1-SNAPSHOT"
   spigotRepository="$HOME/.m2/repository/org/spigotmc/spigot/$snap/spigot-$snap"
   mcSrvRepository="$HOME/.m2/repository/org/spigotmc/minecraft-server/$snap/minecraft-server-$snap"
   shouldRun=false
   for repoFile in "$spigotRepository-remapped-mojang.jar" "$spigotRepository-remapped-obf.jar" "$mcSrvRepository-maps-mojang.txt" "$mcSrvRepository-maps-spigot.csrg"; do
      if [ ! -f "$repoFile" ]; then
         shouldRun=true
         echo "Failed to find file $repoFile."
      fi
   done

   if [ $shouldRun == true ]; then
      if [ -d "$HOME/.m2/repository/org/spigotmc/spigot/$snap" ]; then
         echo "Found incomplete repository for $mcVersion. Running remapping BuildTools ..."
      fi
      if [[ $mcVersion == "1.17" ]]; then
         mcCmd="\"$java16\" -jar BuildTools.jar --rev $mcVersion --remapped"
      else
         mcCmd="\"$java17\" -jar BuildTools.jar --rev $mcVersion --remapped"
      fi
      echo "Running $mcCmd ..."
      eval $mcCmd
      if [ ! -f "spigot-$mcVersion.jar" ]; then # if build failed
         echo "Failed to build version $mcVersion."
         exit 1
      fi
   else
      echo "Repository for $mcVersion already exists. Skipping..."
   fi
done
echo "Installation complete!"
