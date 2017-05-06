# Yalp Store [![Codacy Badge](https://api.codacy.com/project/badge/Grade/5e2be9f71606470fb9da49f91ed6f4a9)](https://www.codacy.com/app/yeriomin/YalpStore) [![codebeat badge](https://codebeat.co/badges/c18f32ef-bf28-4948-8156-9a52e50e121c)](https://codebeat.co/projects/github-com-yeriomin-yalpstore) [![Build Status](https://travis-ci.org/yeriomin/YalpStore.svg?branch=master)](https://travis-ci.org/yeriomin/YalpStore)

<a href="https://f-droid.org/repository/browse/?fdid=com.github.yeriomin.yalpstore" target="_blank">
  <img src="https://f-droid.org/badge/get-it-on.png" height="80"/>
</a>
<br/>
<a href="https://poeditor.com/join/project/LUPUijv2Cs" target="_blank">
  <img src="https://poeditor.com/public/images/logo_small.png" />
</a>

## What does it do?
Yalp Store lets you download apps from Google Play Store **as apk files**. It can search for **updates** of installed apps and lets you **search** for other apps. Yalp saves downloaded apks to your default download folder. Other features include browsing categories, viewing and leaving reviews, black/whitelisting apps for updates, filtering apps by being free/paid and containing/not containing ads.

To supplement the features related to Google Play, Yalp Store has ordinary package manager features: listing, running, installing and uninstalling local apps.

If root is available, Yalp Store can update your apps in background, install and uninstall system apps.

Yalp Store can be installed as a system app to gain background package installation permission. In ths case "Unknown sources" setting can be left off.

## Why would I use it?
If you are content with Google Play Store app, you will not need this app.

The point of Yalp Store is to be small and independent from Google Services Framework. As time passed, Google Services Framework and Google Play Store apps grew in size, which made them almost too big for old phones (Nexus One has 150Mb memory available for apps, half of it would be taken by Google apps). Another reason to use Yalp Store is if you frequently flash experimental ROMs. This often breaks gapps and even prevents their reinstallation. In this situation Yalp will still work.

## How does it work?
Yalp Store uses the same (protobuf) API the android Play Store app uses. It downloads apks **directly** from Google servers.

By default Yalp Store connects to Google services using a built-in account, so **you do not have to own a Google account to use it**. The only reason to use a live Google account is to access the paid apps you own.

## FAQ
>Q: What about buying apps, books, music, movies? Is it going to be implemented at some point?
>
>A: No. Only free apps and apps you have purchased are going to be accessible through Yalp Store. Google Play API is not open and is not documented, so Yalp Store is developed through reverse-engineering. To implement features related to anything that requires payment I would have to (very) frequently buy stuff from Google Play Store. 

>Q: Why isn't Yalp Store using Material Design? Is it going to?
>
>A: No. The priority for Yalp Store is backwards compatibility and size. It works on every android since Android 2.0 Eclair. Implementing Material Design would require adding a heavy dependency and a lot of purely-UI code.

>Q: Is it **legal** to use Yalp Store with my own Google account?
>
>A: No. Yalp Store violates [§3.3 of Google Play Terms of Service](https://www.google.com/mobile/android/market-tos.html). Your account might be disabled, robbing you of any apps you have purchased.

>Q: Is it **safe** to use Yalp Store with my own Google account?
>
>A: Yes. Software like Yalp Store, Google Play Crawler and Raccoon has been used for years and it seems to be safe. Never heard of any real cases of accounts being disabled.

Yalp Store is derived from the following projects:
* https://github.com/Akdeniz/google-play-crawler
* [Raccoon](http://raccoon.onyxbits.de) ([source](https://github.com/onyxbits/raccoon4/))
