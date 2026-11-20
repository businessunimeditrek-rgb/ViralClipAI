# ViralClip AI — Android starter project

This is an Android Studio **UI prototype**, not a finished video editor and not an APK.

## Included
- Native Android app shell using Kotlin + Jetpack Compose
- YouTube URL field and basic URL check
- Local video picker
- Clip length and caption-language selectors
- 9:16 output setting display
- Honest status messages for unfinished processing features

## Not implemented yet
- YouTube video acquisition (must use permitted/authorized sources; do not bypass YouTube restrictions)
- Automatic highlight detection
- Speech-to-text transcription and timed captions
- Subject-aware 9:16 reframing
- MP4 trimming/render/export
- Background processing and progress notifications

## Build
1. Open this folder in Android Studio (recent version).
2. Let Gradle sync and install the Android SDK Platform 35 if prompted.
3. Run on an Android device, or use Build > Build Bundle(s) / APK(s) > Build APK(s).

The build environment used to prepare this project did not have Android SDK/Gradle installed, so an installable APK could not be compiled here.

## Suggested implementation path
- Media3 Transformer for local media editing/export
- On-device speech recognition or an on-device ASR model for captions
- Scene/audio analysis for highlight candidates
- Android MediaStore for saving MP4 output
- Only process videos the user owns or is authorized to edit
