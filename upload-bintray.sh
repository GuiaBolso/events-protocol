#!/bin/bash

modules=("server" "client")

for i in "${modules[@]}"
do
  ./gradlew :$i:clean :$i:build :$i:bintrayUpload -PbintrayUser=$BINTRAY_USER -PbintrayKey=$BINTRAY_KEY -PdryRun=false
done
