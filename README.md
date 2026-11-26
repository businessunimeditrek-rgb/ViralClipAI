# ViralClip AI — Android preview

ViralClip AI is an Android Kotlin + Jetpack Compose preview app.

## Current preview
- YouTube URL format check (does not download YouTube videos)
- Select a local video using Android's document picker
- Choose a target clip duration
- Hindi + English caption target displayed in the UI
- 9:16 vertical MP4 export target displayed in the UI

## Not implemented yet
- Automatic highlight detection
- Actual Hindi/English speech recognition and synchronized captions
- Subject-aware vertical reframing
- MP4 trimming, rendering, and export
- Direct YouTube video acquisition

Only edit videos you own or are authorized to use. The app does not bypass YouTube restrictions.

## Build an APK
GitHub Actions builds a debug APK on pushes to `main` and can also be started manually from the Actions tab. Download the `ViralClipAI-preview-APK` artifact from a successful run.

The APK is a preview shell until the video processing features above are implemented. The app shell itself has no subscription or daily quota; this is not a promise of unlimited AI processing.
