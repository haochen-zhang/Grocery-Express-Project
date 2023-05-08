# Compile our java files in this container
FROM openjdk:17-slim AS builder

COPY server/src /usr/server/src/cs6310/src
COPY client/src /usr/client/src/cs6310/src

WORKDIR /usr/server/src/cs6310
RUN find . -name "*.java" | xargs javac -d ./target
RUN jar cfe server.jar Main -C ./target/ .

WORKDIR /usr/client/src/cs6310
RUN find . -name "*.java" | xargs javac -d ./target
RUN jar cfe client.jar Main -C ./target/ .

# Copy the jar and test scenarios into our final image
FROM openjdk:17-slim
WORKDIR /usr/src/cs6310
COPY test_scenarios ./
COPY test_results ./
COPY --from=builder /usr/server/src/cs6310/server.jar ./server.jar
COPY --from=builder /usr/client/src/cs6310/client.jar ./client.jar
RUN apt update && apt install tmux -y
# /usr/local/openjdk-17/bin/java
# RUN apt-get update && apt-get install -y openssh-server sudo
# RUN useradd -rm -d /home/test -s /bin/bash -g root -G sudo -u 1000 test
# RUN echo 'test:test' | chpasswd
# RUN service ssh start
# EXPOSE 22
# CMD ["/usr/sbin/sshd", "-D"]
# CMD ["java", "-jar", "drone_delivery.jar", "commands_00.txt"]
