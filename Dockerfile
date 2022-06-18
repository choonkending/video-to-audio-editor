FROM openjdk:11-buster AS dependencies

RUN apt-get update && apt-get install --yes --no-install-recommends apt-transport-https curl gnupg
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
RUN apt-get update && apt-get install --yes --no-install-recommends ffmpeg sbt \
  && rm -rf /var/lib/apt/lists/*
# useradd is a command to create a new user account in unix. -m creates a home directory, e.g. /home/username
RUN useradd -m build
# Sets the user name (or UID) to use when running the image and for any RUN, CMD and ENTRYPOINT instructions that follow it in the Dockerfile
USER build
RUN mkdir /home/build/app
WORKDIR /home/build/app
# COPY --chown=<user>:<group> <hostPath> <containerPath>
COPY --chown=build:build build.sbt ./
COPY --chown=build:build project/build.properties project/plugins.sbt project/
RUN sbt compile

FROM dependencies as build
COPY --chown=build:build src src
RUN ["sbt", "clean", "stage"]

FROM build as publish

ENV VIDEO_DIRECTORY=data/mp4
ENV AUDIO_DIRECTORY=data/mp3
ENV PREPEND_TEMPLATE_DIRECTORY=data/prepend_templates
ENV PREPEND_INPUT_DIRECTORY=data/prepend_input
ENV PREPEND_OUTPUT_DIRECTORY=data/prepend_output

ENTRYPOINT ["./target/universal/stage/bin/video-and-audio-editor"]
