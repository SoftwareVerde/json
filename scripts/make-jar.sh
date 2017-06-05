#!/bin/bash

rm -rf out/bin 2>/dev/null
mkdir -p out/bin

version='0.1.0'

./gradlew makeJar && cp build/libs/json-${version}.jar out/bin/json-${version}.jar && chmod 770 out/bin/json-${version}.jar

