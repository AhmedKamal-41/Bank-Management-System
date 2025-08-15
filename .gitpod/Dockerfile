FROM gitpod/workspace-full:latest

USER root

RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-21-jdk \
    mysql-server \
    xvfb fluxbox \
    x11vnc \
    websockify \
    novnc \
    && rm -rf /var/lib/apt/lists/*

# Make noVNC proxy easy to call
RUN ln -s /usr/share/novnc/utils/novnc_proxy /usr/local/bin/novnc_proxy || true

# MySQL runtime dir
RUN mkdir -p /run/mysqld && chown mysql:mysql /run/mysqld

ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

USER gitpod
WORKDIR /workspace
