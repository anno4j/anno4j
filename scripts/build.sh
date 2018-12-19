#!/bin/bash
set -ev
COMMITMSG=$(git log --format=%B -n 1 ${TRAVIS_COMMIT})

if [ "${TRAVIS_PULL_REQUEST}" == "false" ] && [ "${TRAVIS_BRANCH}" == "develop" ]; then
   echo "Deploy develop snapshot of Anno4j:" $COMMITMSG ;
   mvn clean deploy --settings settings.xml
elif [ "${TRAVIS_PULL_REQUEST}" == "false" ] && [ "${TRAVIS_BRANCH}" == "master" ]; then
    if [[ $COMMITMSG != \[maven-release-plugin\]* ]]; then
        echo "Release Anno4j:" $COMMITMSG
        echo "Modify git configuration"
        git config --global user.email "anno4jci@web.de"
        git config --global user.name "anno4jbuildserver"
        echo "Add github remote repository"
        git remote rm origin
        git remote add origin https://anno4jbuildserver:$GITHUB_PW@github.com/anno4j/anno4j.git
        echo "Checkout master branch"
        git checkout master || git checkout -b master
        echo "Decrypt gpg keyrings"
        openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in keyrings/secring.gpg.enc -out keyrings/local.secring.gpg -d
        openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in keyrings/pubring.gpg.enc -out keyrings/local.pubring.gpg -d
        echo "Set permissions"
        ls -al keyrings
        chmod a+rx keyrings/local.pubring.gpg
        chmod a+rx keyrings/local.secring.gpg
        ls -al keyrings
        mvn clean release:prepare release:perform -DignoreSnapshots=true -B --settings settings.xml
    else
        echo "Skip TravisCI commit:" $COMMITMSG
    fi
else
   echo "Verify Anno4j:" $COMMITMSG;
   mvn clean verify --settings settings.xml
fi