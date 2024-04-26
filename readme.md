###

### Run in docker

    docker run -v ${PWD}/h2db:/h2db \
        --env SPRING_DATASOURCE_URL=jdbc:h2:/h2db/db \
        --env DISCORD_BOT_TOKEN=token \
        --env DISCORD_CHANNEL_URL=https://discord.com/channels/1234/5678 \
        hdghg/rbmon2

### Run natively

#### Prerequisites ubuntu

    sudo apt-get install build-essential libz-dev zlib1g-dev

#### Compile native image

    ./gradlew clean nativeImage

#### If compilation fails with error executing native-image

Probably because gradle unzips graalvm distro without symlinks creation

    cd ~/.gradle/jdks/graalvm_community-22-amd64-linux/graalvm-community-openjdk-22.0.1+8.1/bin

    # del empty files
    rm native-image
    rm native-image-configure

    # create correct symlinks
    ln -s ../lib/svm/bin/native-image native-image
    ln -s ../lib/svm/bin/native-image-configure native-image-configure
