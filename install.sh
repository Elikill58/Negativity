#!/bin/bash

trap "exit" INT

DIR="spigot/spigotJars/install"
if [ -d "$DIR" ]; then
	echo "$DIR already created" > /dev/null
else
	mkdir -p $DIR
fi
cd $DIR

java8=unknow
java16=unknow
java17=unknow

if [[ $java8 == *"unknow"* || $java16 == *"unknow"* || $java17 == *"unknow"* ]]; then # if at least one JAVA is NOT set
	export IFS=";"
	for javaVersionPath in $JAVA_HOME; do
	    if [[ $javaVersionPath == *"1.8"* ]]; then # check if has "1.8"
	    	java8="$javaVersionPath\\bin\\java.exe"
	    elif [[ $javaVersionPath == *"16."* ]]; then # check if has "16."
	    	java16="$javaVersionPath\\java.exe"
	    elif [[ $javaVersionPath == *"17."* ]]; then # check if has "17."
	    	java17="$javaVersionPath\\java.exe"
	    fi
	done

	if [[ $java8 == *"unknow"* ]]; then
		if [ -d "./jdk-8" ]; then
			echo "Failed to find Java 8 and already downloaded. Use unzipped version ..."
		else
			echo "Failed to find Java 8. Download it ..."
			curl -o Java8.zip https://download.java.net/openjdk/jdk8u42/ri/openjdk-8u42-b03-windows-i586-14_jul_2022.zip
			unzip Java8.zip
		fi
	    java8="$PWD\\jdk-8\\bin\\java.exe"
	fi
	if [[ $java16 == *"unknow"* ]]; then
		if [ -d "./jdk-16" ]; then
			echo "Failed to find Java 16 and already downloaded. Use unzipped version ..."
		else
			echo "Failed to find Java 16. Download it ..."
			curl -o Java16.zip https://download.java.net/openjdk/jdk16/ri/openjdk-16+36_windows-x64_bin.zip
			unzip Java16.zip
		fi
	    java16="$PWD\\jdk-16\\bin\\java.exe"
	fi
	if [[ $java17 == *"unknow"* ]]; then
		if [ -d "./jdk-17" ]; then
			echo "Failed to find Java 17 and already downloaded. Use unzipped version ..."
		else
			echo "Failed to find Java 17. Download it ..."
			curl -o Java17.zip https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_windows-x64_bin.zip
			unzip Java17.zip
		fi
	    java17="$PWD\\jdk-17\\bin\\java.exe"
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

curl -z BuildTools.jar -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
for mcVersion in "1.8.8" "1.9.2" "1.9.4" "1.10.2" "1.11.2" "1.12" "1.13" "1.13.2" "1.14.4" "1.15" "1.16.1" "1.16.4"; do
	spigotFile=spigot-$mcVersion.jar
	if [ -f "../$spigotFile" ]; then
	   echo "Spigot $mcVersion already exist. Skipping ..."
	else
		mcCmd="\"$java8\" -jar BuildTools.jar --rev $mcVersion"
		echo "Running $mcCmd ..."
		eval $mcCmd
		if [ -f "$spigotFile" ]; then # if build successful
			cp $spigotFile "../$spigotFile"
		else
			echo "Failed to build version $mcVersion."
			exit 500
		fi
	fi
done
if [ ! -f "../spigot-0-1.13.jar" ]; then
	cp "../spigot-1.13.jar" "../spigot-0-1.13.jar" # the first jar of the list
	echo "Copied 1.13 jar file to first item of list."
fi
for mcVersion in "1.17" "1.18" "1.18.2" "1.19"; do # all java17 remapped versions
	snap="$mcVersion-R0.1-SNAPSHOT"
	spigotRepository="$(eval echo "~")/.m2/repository/org/spigotmc/spigot/$snap/spigot-$snap"
	mcSrvRepository="$(eval echo "~")/.m2/repository/org/spigotmc/minecraft-server/$snap/minecraft-server-$snap"
	shouldRun=false
	for repoFile in "$spigotRepository-remapped-mojang.jar" "$spigotRepository-remapped-obf.jar" "$mcSrvRepository-maps-mojang.txt" "$mcSrvRepository-maps-spigot.csrg"; do
		if [ ! -f "$repoFile" ]; then
			shouldRun=true
			echo "Failed to find file $repoFile."
		fi
	done

	if [ $shouldRun == true ]; then
		if [ -d "$(eval echo "~")/.m2/repository/org/spigotmc/spigot/$snap" ]; then
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
			exit 500
		fi
	else
		echo "Repository for $mcVersion already exist. Skipping..."
	fi
done
echo "Installation complete !"