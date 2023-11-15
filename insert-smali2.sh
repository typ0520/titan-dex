#!/bin/bash

if [ ! -e app/build/outputs/apk/release/app-release-unsigned.apk  ];then
  ./gradlew :app:assR
fi

/opt/homebrew/bin/apktool d -f --no-src app/build/outputs/apk/release/app-release-unsigned.apk -o app/build/outputs/apk/release/app-release-unsigned

rm app/build/outputs/apk/release/app-release-unsigned/classes.dex

cp .test/out.dex app/build/outputs/apk/release/app-release-unsigned/classes.dex

/opt/homebrew/bin/apktool b app/build/outputs/apk/release/app-release-unsigned -o app/build/outputs/apk/release/app-release-unsigned2.apk

$ANDROID_HOME/build-tools/30.0.3/zipalign -p -f -v 4 app/build/outputs/apk/release/app-release-unsigned2.apk app/build/outputs/apk/release/app-release-unsigned2-zipalign.apk
yes 111111 | $ANDROID_HOME/build-tools/30.0.3/apksigner sign --ks ./test.keystore --ks-key-alias test --out app/build/outputs/apk/release/app-release-signed.apk app/build/outputs/apk/release/app-release-unsigned2-zipalign.apk

adb install -r app/build/outputs/apk/release/app-release-signed.apk