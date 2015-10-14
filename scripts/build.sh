#!/bin/bash
set -ev

if [ "${TRAVIS_PULL_REQUEST}" == "false" ] && [ "${TRAVIS_BRANCH}" == "develop" ]; then
   echo "Deploy develop snapshot of Anno4j";
   mvn clean deploy  -DskipTests=true --settings settings.xml
elif [ "${TRAVIS_PULL_REQUEST}" == "false" ] && [ "${TRAVIS_BRANCH}" == "v2-develop" ]; then
   echo "Deploy develop v2 snapshot of Anno4j";
   mvn clean deploy  -DskipTests=true --settings settings.xml
elif [ "${TRAVIS_PULL_REQUEST}" == "false" ] && [ "${TRAVIS_BRANCH}" == "master" ]; then
   echo "Release Anno4j";
   mvn clean release:prepare release:perform -DignoreSnapshots=true -DskipTests=true -B --settings settings.xml
else
   echo "Verify Anno4j";
   mvn clean verify -DskipTests=true --settings settings.xml
fi