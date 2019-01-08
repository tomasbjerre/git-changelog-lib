#!/bin/bash
./gradlew release -d || exit 1
./build.sh
git commit -a --amend --no-edit
git push -f
