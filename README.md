# ViralClip AI — Android video editor preview

## Implemented in the current source
- Android Kotlin + Jetpack Compose interface
- Select a local video from the phone
- Read source video duration
- Set clip start and end times
- Trim the selected section using AndroidX Media3 Transformer
- Convert/export to a 1080 × 1920 (9:16) vertical MP4 using crop-to-fill
- Save the finished MP4 using Android's system save dialog
- No watermark is added by this app; processing is local on the phone

## Not implemented yet
- AI detection of the most engaging moments
- Automatic speech-to-text captions in Hindi and English
- Caption burn-in and styling
- Automatic face/person tracking and reframing
- Direct YouTube downloading. YouTube links are opened in YouTube; the app does not bypass download restrictions.

Use only videos you own or are authorized to edit. Export speed and supported input formats depend on the phone's hardware and Android media codecs. The current editor is a manual trim-and-export MVP, not a finished AI highlight generator.

## Build
GitHub Actions builds a debug APK on pushes to `main` and can also be started manually from the Actions tab. Download the `ViralClipAI-preview-APK` artifact from a successful run.
