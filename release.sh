#!/bin/bash
set -x

#
# Getting classpath issues when using this together with git-changelog-gradle-plugin
#

nextVersion=$(npx git-changelog-command-line \
 --print-next-version) \
 && ./gradlew release -Dorg.gradle.project.version=$nextVersion \
 && git commit -a -m "chore(release): ${nextVersion} [GRADLE SCRIPT]" \
 && git tag $nextVersion \
 && git push --follow-tags \
 && npx git-changelog-command-line \
  -of CHANGELOG.md \
  --ignore-pattern "^\\[maven-release-plugin\\].*|^\\[Gradle Release Plugin\\].*|^Merge.*|.*\\[GRADLE SCRIPT\\].*" \
 && git commit -a -m "chore(release): Updating changelog with ${nextVersion} [GRADLE SCRIPT]" \
 && git push
