# Git Changelog changelog

Changelog of Git Changelog.

## Next release
### Other changes

**Bugfix, crashed if all commits in tag were ignored**


[12e7f8dd9e95e7f](https://github.com/tomasbjerre/git-changelog-lib/commit/12e7f8dd9e95e7f) Tomas Bjerre *2016-02-14 17:32:30*


## 1.28
### Other changes

**Optimizations, reducing memory usage**


[f6eee5c82a46a54](https://github.com/tomasbjerre/git-changelog-lib/commit/f6eee5c82a46a54) Tomas Bjerre *2016-02-14 16:33:19*

**Updating CHANGELOG.md**


[58fcebc50354375](https://github.com/tomasbjerre/git-changelog-lib/commit/58fcebc50354375) Tomas Bjerre *2016-02-13 17:14:37*


## 1.27
### Other changes

**Bugfix, was not including first tag**


[ca96998dbd2f376](https://github.com/tomasbjerre/git-changelog-lib/commit/ca96998dbd2f376) Tomas Bjerre *2016-02-13 08:52:27*


## 1.26
### GitHub [#13](https://github.com/tomasbjerre/git-changelog-lib/issues/13) Performance

**Rewriting GitRepo to make it faster**


[60811d245d20669](https://github.com/tomasbjerre/git-changelog-lib/commit/60811d245d20669) Tomas Bjerre *2016-02-13 08:24:24*

**Identified performance issue as GitRepo:getTags()**

 * Updating performance test to reveal it. 

[be9fb93023f13ad](https://github.com/tomasbjerre/git-changelog-lib/commit/be9fb93023f13ad) Tomas Bjerre *2016-02-11 17:48:06*


## 1.25
### GitHub [#13](https://github.com/tomasbjerre/git-changelog-lib/issues/13) Performance

**Letting JGit determine new commits between refs**

 * Also changing changelog template. 
 * Also trimming messageTitle variable. 

[5b307bd00b47e83](https://github.com/tomasbjerre/git-changelog-lib/commit/5b307bd00b47e83) Tomas Bjerre *2016-02-10 17:31:13*


## 1.24
### Other changes

**Added variables: messageTitle, messageBody, messageItems**


[80609aa22d11adf](https://github.com/tomasbjerre/git-changelog-lib/commit/80609aa22d11adf) Tomas Bjerre *2016-02-09 19:12:15*

**Maven Central version badge in README.md**


[4e25e7e2cf5d5ec](https://github.com/tomasbjerre/git-changelog-lib/commit/4e25e7e2cf5d5ec) Tomas Bjerre *2016-01-31 21:12:35*

**Better error message when commit not found**


[1e5cb6c76a681aa](https://github.com/tomasbjerre/git-changelog-lib/commit/1e5cb6c76a681aa) Tomas Bjerre *2016-01-31 12:15:00*


## 1.23
### Other changes

**Sorting tags by committime, not committime formatted string**


[a9fc5a30a82c320](https://github.com/tomasbjerre/git-changelog-lib/commit/a9fc5a30a82c320) Tomas Bjerre *2016-01-31 10:30:24*

**Implementing toString on API mode objects**


[283c20a3ae4b7b6](https://github.com/tomasbjerre/git-changelog-lib/commit/283c20a3ae4b7b6) Tomas Bjerre *2016-01-31 10:10:30*

**Lgging exception in rest client**


[e529afb2a911123](https://github.com/tomasbjerre/git-changelog-lib/commit/e529afb2a911123) Tomas Bjerre *2016-01-31 08:04:33*


## 1.22
### Other changes

**Removing accidently added duplicate Gson dependency**


[7fde8935d59456d](https://github.com/tomasbjerre/git-changelog-lib/commit/7fde8935d59456d) Tomas Bjerre *2016-01-30 17:07:40*


## 1.21
### Other changes

**Correcting revision logging**

 * Resetting Jira and GitHub clients before tests. Was having troubles with the cache not being invalidated between tests. 

[00b37d11101f8d1](https://github.com/tomasbjerre/git-changelog-lib/commit/00b37d11101f8d1) Tomas Bjerre *2016-01-30 16:39:21*


## 1.20
### Other changes

**Including first commit**

 * Was excluding it when ZERO_COMMIT constant was used 

[c3da07c0286855d](https://github.com/tomasbjerre/git-changelog-lib/commit/c3da07c0286855d) Tomas Bjerre *2016-01-30 11:56:15*

**Documentation**


[0038494e3452b52](https://github.com/tomasbjerre/git-changelog-lib/commit/0038494e3452b52) Tomas Bjerre *2016-01-30 09:32:00*

**Adding test cases to GitRepo**


[96719f26ed22419](https://github.com/tomasbjerre/git-changelog-lib/commit/96719f26ed22419) Tomas Bjerre *2016-01-28 19:53:50*

**Updating CHANGELOG.md**

 * And Removing generate_changelog.sh 

[7613efc4a5a0833](https://github.com/tomasbjerre/git-changelog-lib/commit/7613efc4a5a0833) Tomas Bjerre *2016-01-27 18:51:11*


## 1.19
### GitHub [#11](https://github.com/tomasbjerre/git-changelog-lib/issues/11) Move command line to its own repo

**Removing command line code**


[a7b20fdea564ff2](https://github.com/tomasbjerre/git-changelog-lib/commit/a7b20fdea564ff2) Tomas Bjerre *2016-01-27 18:14:33*


### Other changes

**Changing Jenkins plugin link to point at JenkinsCI**


[d405f4e8ae5f34f](https://github.com/tomasbjerre/git-changelog-lib/commit/d405f4e8ae5f34f) Tomas Bjerre *2015-12-11 07:56:23*

**Adding example template to readme**


[296d811a7ec3492](https://github.com/tomasbjerre/git-changelog-lib/commit/296d811a7ec3492) Tomas Bjerre *2015-12-10 17:12:44*


## 1.18
### Other changes

**Downgrading JGIT to 3.6.2 to be compatible with its older API**


[25fad813507296c](https://github.com/tomasbjerre/git-changelog-lib/commit/25fad813507296c) Tomas Bjerre *2015-12-08 18:44:25*


## 1.17
### Other changes

**Allowing variables to be extended with custom context:s**


[c7e3a39354cb339](https://github.com/tomasbjerre/git-changelog-lib/commit/c7e3a39354cb339) Tomas Bjerre *2015-12-05 14:30:21*


## 1.16
### Other changes

**Peeling references**

 * To support annotated tags 

[b818fd6a2f1207c](https://github.com/tomasbjerre/git-changelog-lib/commit/b818fd6a2f1207c) Tomas Bjerre *2015-12-05 14:01:56*


## 1.15
### Other changes

**Using JGit to find repo folder correctly**

 * And JsonPath 2.1.0 

[2d0c1f1102387d0](https://github.com/tomasbjerre/git-changelog-lib/commit/2d0c1f1102387d0) Tomas Bjerre *2015-12-04 22:09:16*


## 1.14
### Other changes

**Caching requests to GitHub and Jira**


[2b9b6112244d1aa](https://github.com/tomasbjerre/git-changelog-lib/commit/2b9b6112244d1aa) Tomas Bjerre *2015-12-01 21:34:52*


## 1.13
### Other changes

**Avoiding crash if GitHub issue cant be found**


[abe713cb069aa1f](https://github.com/tomasbjerre/git-changelog-lib/commit/abe713cb069aa1f) Tomas Bjerre *2015-11-23 21:44:23*


## 1.12
### GitHub [#2](https://github.com/tomasbjerre/git-changelog-lib/issues/2) GitHub Integration

**Integrating with GitHub**


[45af766856ac703](https://github.com/tomasbjerre/git-changelog-lib/commit/45af766856ac703) Tomas Bjerre *2015-11-22 19:51:40*


### GitHub [#3](https://github.com/tomasbjerre/git-changelog-lib/issues/3) Jira Integration

**Integrating with Jira**


[30a1095331ded15](https://github.com/tomasbjerre/git-changelog-lib/commit/30a1095331ded15) Tomas Bjerre *2015-11-23 17:32:23*


## 1.11
### Other changes

**Changing master reference constan, to just master**

 * refs/heads/master may not exist, perhaps its refs/remotes/origin/master 

[c655cbd914d06f3](https://github.com/tomasbjerre/git-changelog-lib/commit/c655cbd914d06f3) Tomas Bjerre *2015-11-21 15:36:03*

**doc**


[8c95fd4870872d4](https://github.com/tomasbjerre/git-changelog-lib/commit/8c95fd4870872d4) Tomas Bjerre *2015-11-21 14:24:35*


## 1.10
### Other changes

**Adding test cases for MediaWiki integration**

 * Linking readme to screenshots 
 * Not encoding html-tags for MediaWiki, to enable setting the toclimit with a tag. 

[c8da374a92183d2](https://github.com/tomasbjerre/git-changelog-lib/commit/c8da374a92183d2) Tomas Bjerre *2015-11-21 14:18:19*

**Adding example html-template**


[7749ddfa42b4034](https://github.com/tomasbjerre/git-changelog-lib/commit/7749ddfa42b4034) Tomas Bjerre *2015-11-20 21:38:13*


## 1.9
### Bugs #bug 

**Adding custom issues correctly fix**


[b5f03710018a65d](https://github.com/tomasbjerre/git-changelog-lib/commit/b5f03710018a65d) Tomas Bjerre *2015-11-20 21:12:55*


### Other changes

**Better regular expression for extracting readable part of tag name #feature**


[d669ae9e7d00e52](https://github.com/tomasbjerre/git-changelog-lib/commit/d669ae9e7d00e52) Tomas Bjerre *2015-11-20 20:34:55*


## 1.8
### Bugs #bug 

**Setting default setting of ignore commits regexp fix**


[c226865b64ce01c](https://github.com/tomasbjerre/git-changelog-lib/commit/c226865b64ce01c) Tomas Bjerre *2015-11-20 20:24:08*


## 1.7
### Bugs #bug 

**Using correct reference fix**


[388a3a851f665c1](https://github.com/tomasbjerre/git-changelog-lib/commit/388a3a851f665c1) Tomas Bjerre *2015-11-20 19:08:45*


## 1.6
### Other changes

**Some more work to get the lib working with Jenkins plugin**


[bfa6753630efcdb](https://github.com/tomasbjerre/git-changelog-lib/commit/bfa6753630efcdb) Tomas Bjerre *2015-11-20 17:03:04*

**Better error if not setting fromRepo**


[f978c6fd9c72233](https://github.com/tomasbjerre/git-changelog-lib/commit/f978c6fd9c72233) Tomas Bjerre *2015-11-19 21:21:04*


## 1.5
### Other changes

**Not loading default settings by default when using API**


[d3e8656074af987](https://github.com/tomasbjerre/git-changelog-lib/commit/d3e8656074af987) Tomas Bjerre *2015-11-19 20:56:47*

**updating changelog**


[64f318ca8f10ba8](https://github.com/tomasbjerre/git-changelog-lib/commit/64f318ca8f10ba8) Tomas Bjerre *2015-11-19 19:46:44*


## 1.4
### GitHub [#7](https://github.com/tomasbjerre/git-changelog-lib/issues/7) Add booleans to enable if statements

**Adding hasIssue hasLink to readme doc**


[fbb455921921ddc](https://github.com/tomasbjerre/git-changelog-lib/commit/fbb455921921ddc) Tomas Bjerre *2015-11-18 19:41:03*


### Other changes

**Making sure several custom issue patterns can be set with API**


[49c51f0fa73f35c](https://github.com/tomasbjerre/git-changelog-lib/commit/49c51f0fa73f35c) Tomas Bjerre *2015-11-19 19:40:27*

**Updating mediawiki screenshot**


[1669b02acf8c9fb](https://github.com/tomasbjerre/git-changelog-lib/commit/1669b02acf8c9fb) Tomas Bjerre *2015-11-18 21:00:59*


## 1.3
### GitHub [#7](https://github.com/tomasbjerre/git-changelog-lib/issues/7) Add booleans to enable if statements

**Adding booleans to check if link and/or issue exists in issue**


[210e963a07b8a7b](https://github.com/tomasbjerre/git-changelog-lib/commit/210e963a07b8a7b) Tomas Bjerre *2015-11-18 19:06:00*


### Other changes

**Adding new mediawiki screenshot**


[467d126c87b734e](https://github.com/tomasbjerre/git-changelog-lib/commit/467d126c87b734e) Tomas Bjerre *2015-11-18 19:17:48*

**Update README.md**


[49afd799253a777](https://github.com/tomasbjerre/git-changelog-lib/commit/49afd799253a777) Tomas Bjerre *2015-11-18 06:55:50*

**doc**


[a083453437d20bd](https://github.com/tomasbjerre/git-changelog-lib/commit/a083453437d20bd) Tomas Bjerre *2015-11-17 20:29:42*


## 1.2
### GitHub [#4](https://github.com/tomasbjerre/git-changelog-lib/issues/4) Mediawiki integration

**MediaWiki integration**


[f45829b2c6a78d7](https://github.com/tomasbjerre/git-changelog-lib/commit/f45829b2c6a78d7) Tomas Bjerre *2015-11-17 19:27:03*


### Other changes

**doc**


[440ba52998293de](https://github.com/tomasbjerre/git-changelog-lib/commit/440ba52998293de) Tomas Bjerre *2015-11-15 15:08:56*

**Adding link to Maven plugin**


[8fe5a0b66660e6d](https://github.com/tomasbjerre/git-changelog-lib/commit/8fe5a0b66660e6d) Tomas Bjerre *2015-11-15 14:39:41*

**Doc**


[47748d4350e9559](https://github.com/tomasbjerre/git-changelog-lib/commit/47748d4350e9559) Tomas Bjerre *2015-11-15 13:16:55*

**Changing regexp pattern for github**

 * So that it does not match feature issues #feature 

[dd31f0a16fedfcf](https://github.com/tomasbjerre/git-changelog-lib/commit/dd31f0a16fedfcf) Tomas Bjerre *2015-11-15 12:47:06*

**Better error message if no template specified #feature**


[a599d143f676b92](https://github.com/tomasbjerre/git-changelog-lib/commit/a599d143f676b92) Tomas Bjerre *2015-11-15 12:42:08*

**Prepare for next release**


[6dd3418a6dc5b0d](https://github.com/tomasbjerre/git-changelog-lib/commit/6dd3418a6dc5b0d) Tomas Bjerre *2015-11-15 11:41:48*


## 1.1
### Bugs #bug 

**Adding generated CHANGELOG.md #feature**

 * Correcting faulty precondition check, file output argument could not be set. 

[b3ddd5fdad30f41](https://github.com/tomasbjerre/git-changelog-lib/commit/b3ddd5fdad30f41) Tomas Bjerre *2015-11-15 09:48:35*

**Adding script to generate changelog**

 * Finding git repo in parent folders correctly fix 

[82be7c398d445bd](https://github.com/tomasbjerre/git-changelog-lib/commit/82be7c398d445bd) Tomas Bjerre *2015-11-15 09:15:38*


### Other changes

**Car remove issue from commit message in changelog #feature**


[ec026ad2f25c612](https://github.com/tomasbjerre/git-changelog-lib/commit/ec026ad2f25c612) Tomas Bjerre *2015-11-15 11:37:18*

**Improving test output #feature**


[3191a431aff2b20](https://github.com/tomasbjerre/git-changelog-lib/commit/3191a431aff2b20) Tomas Bjerre *2015-11-15 10:58:51*

**Customizing changelog #feature**


[59830c6f2a394fe](https://github.com/tomasbjerre/git-changelog-lib/commit/59830c6f2a394fe) Tomas Bjerre *2015-11-15 09:24:58*

**More settings can be set from command line**

 * More testing 

[ba9d565ddd15d1b](https://github.com/tomasbjerre/git-changelog-lib/commit/ba9d565ddd15d1b) Tomas Bjerre *2015-11-15 08:58:00*


## 1.0
### Other changes

**Doc**


[83a8a7b2f96ba88](https://github.com/tomasbjerre/git-changelog-lib/commit/83a8a7b2f96ba88) Tomas Bjerre *2015-11-14 19:36:21*

**Test**


[5c54861708099d9](https://github.com/tomasbjerre/git-changelog-lib/commit/5c54861708099d9) Tomas Bjerre *2015-11-14 19:00:17*

**Work for 1.0**


[051effe72405e78](https://github.com/tomasbjerre/git-changelog-lib/commit/051effe72405e78) Tomas Bjerre *2015-11-14 12:14:41*

**Initial commit**


[5aaeb907f68915a](https://github.com/tomasbjerre/git-changelog-lib/commit/5aaeb907f68915a) Tomas Bjerre *2015-11-14 09:46:23*

**Initial commit**


[a1aa5ff5b625e63](https://github.com/tomasbjerre/git-changelog-lib/commit/a1aa5ff5b625e63) Tomas Bjerre *2015-11-12 05:29:31*


