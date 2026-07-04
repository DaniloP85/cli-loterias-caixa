FROM node:20-alpine

RUN apk add --no-cache bash git curl libc6-compat gcompat openjdk21-jdk

RUN npm install -g @anthropic-ai/claude-code

RUN mkdir -p /home/node/.claude && chown -R node:node /home/node/.claude

ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

USER node
WORKDIR /workspace