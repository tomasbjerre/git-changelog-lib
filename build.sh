#!/bin/bash
cp src/main/resources/git-changelog-template.mustache changelog.mustache
./gradlew clean
mkdir build
./gradlew googleErrorProne > build/googleErrorProne.log 2>&1
./gradlew build -i
./gradlew test -Dtest.single=GitChangelogTest
