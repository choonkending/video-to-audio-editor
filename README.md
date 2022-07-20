# Video to audio editor

This is a Scala program written to do the following tasks:
- Convert MP4 to MP3
- Stitch audio files to the beginning of an mp3 file


## Instructions

1. Set up folder structure
    ```
    mkdir -p data/mp4 data/mp3 data/prepend_input data/prepend_output data/prepend_templates
    ```
1. Run docker

    ```
    docker run -it -v ${PWD}:/home/build/app \
    -v excluded_target:/home/build/app/target \
    kending/video-to-audio-editor:latest bash
    ```
