#!/bin/bash

#./gradlew :app:assR
#
#/opt/homebrew/bin/apktool d app/build/outputs/apk/release/app-release-unsigned.apk -o app/build/outputs/apk/release/app-release-unsigned

mkdir -p app/build/outputs/apk/release/app-release-unsigned/smali/mock

cp ErrorMock.smali app/build/outputs/apk/release/app-release-unsigned/smali/mock/ErrorMock.smali
#插桩

/opt/homebrew/bin/apktool b app/build/outputs/apk/release/app-release-unsigned -o app/build/outputs/apk/release/app-release-unsigned2.apk

/Users/tong/Library/Android/sdk/build-tools/30.0.3/zipalign -p -f -v 4 app/build/outputs/apk/release/app-release-unsigned2.apk app/build/outputs/apk/release/app-release-unsigned2-zipalign.apk
/Users/tong/Library/Android/sdk/build-tools/30.0.3/apksigner sign --ks ./test.keystore --ks-key-alias test --out app/build/outputs/apk/release/app-release-signed.apk app/build/outputs/apk/release/app-release-unsigned2-zipalign.apk
