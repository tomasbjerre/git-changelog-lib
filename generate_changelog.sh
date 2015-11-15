#/bin/bash
cd build/distributions
unzip *T.zip
cd *T
./bin/git-changelog-lib -std
