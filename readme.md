###

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
