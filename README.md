<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

[![Build APK](https://github.com/JGames-studio/Ai-justice-buddy-/actions/workflows/build-apk.yml/badge.svg)](https://github.com/JGames-studio/Ai-justice-buddy-/actions/workflows/build-apk.yml)

Download the latest test APK from the [GitHub Releases page](https://github.com/JGames-studio/Ai-justice-buddy-/releases).

## Install the test APK on Android

1. On your Android device, allow installs from unknown sources (or enable **Install unknown apps** for your browser/files app).
2. Download `AI-Justice-Buddy-debug.apk` from the latest GitHub release.
3. Open the downloaded APK and confirm the installation prompts.

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/6399ac6e-fb58-41d5-864c-ae702a2ae41d

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
