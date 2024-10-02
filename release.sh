#!/bin/bash

nextVersion=$(npx git-changelog-command-line \
 --print-next-version) \
 && ./gradlew release -Pversion=$nextVersion \
 && npx git-changelog-command-line \
  -of CHANGELOG.md \
  --ignore-pattern "^\\[maven-release-plugin\\].*|^\\[Gradle Release Plugin\\].*|^Merge.*|.*\\[GRADLE SCRIPT\\].*"
