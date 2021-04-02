#!/bin/bash
cp src/main/resources/git-changelog-template.mustache changelog.mustache
./gradlew build
