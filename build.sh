#!/bin/bash
cp src/main/resources/git-changelog-template.mustache changelog.mustache
./gradlew clean build -i
./gradlew test -Dtest.single=GitChangelogTest
