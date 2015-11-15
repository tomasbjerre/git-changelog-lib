#/bin/bash
ROOT_FOLDER=`pwd`
cd build/distributions
unzip *T.zip
cd *T
./bin/git-changelog-lib -std -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json
