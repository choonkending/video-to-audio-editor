# Video to audio editor

This is a Scala program written to do the following tasks:
- Convert MP4 to MP3
- Stitch audio files to the beginning of an mp3 file


## Instructions

1.

```
docker run -it -v ${PWD}:/home/build/app \
-v excluded_target:/home/build/app/target \
kending/video-to-audio-editor:latest bash
```
