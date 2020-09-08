#!/bin/bash

modules=("tracing" "core" "server" "client" "test", "ktor")

for i in "${modules[@]}"
do
  ./gradlew :${i}:clean :${i}:build :${i}:bintrayUpload -PbintrayUser=${bintray_user} -PbintrayKey=${bintray_key} -PdryRun=false
done
