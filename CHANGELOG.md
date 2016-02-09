# Git Changelog changelog

Changelog of Git Changelog.

## Next release
### Other changes

[04f9efa398d3834](https://github.com/tomasbjerre/git-changelog-lib/commit/04f9efa398d3834) Tomas Bjerre *2016-02-09 19:09:13*

Added variables: messageTitle, messageBody, messageItems

[4e25e7e2cf5d5ec](https://github.com/tomasbjerre/git-changelog-lib/commit/4e25e7e2cf5d5ec) Tomas Bjerre *2016-01-31 21:12:35*

Maven Central version badge in README.md

[1e5cb6c76a681aa](https://github.com/tomasbjerre/git-changelog-lib/commit/1e5cb6c76a681aa) Tomas Bjerre *2016-01-31 12:15:00*

Better error message when commit not found


## 1.23
### Other changes

[a9fc5a30a82c320](https://github.com/tomasbjerre/git-changelog-lib/commit/a9fc5a30a82c320) Tomas Bjerre *2016-01-31 10:30:24*

Sorting tags by committime, not committime formatted string

[283c20a3ae4b7b6](https://github.com/tomasbjerre/git-changelog-lib/commit/283c20a3ae4b7b6) Tomas Bjerre *2016-01-31 10:10:30*

Implementing toString on API mode objects

[e529afb2a911123](https://github.com/tomasbjerre/git-changelog-lib/commit/e529afb2a911123) Tomas Bjerre *2016-01-31 08:04:33*

Lgging exception in rest client


## 1.22
### Other changes

[7fde8935d59456d](https://github.com/tomasbjerre/git-changelog-lib/commit/7fde8935d59456d) Tomas Bjerre *2016-01-30 17:07:40*

Removing accidently added duplicate Gson dependency


## 1.21
### Other changes

[00b37d11101f8d1](https://github.com/tomasbjerre/git-changelog-lib/commit/00b37d11101f8d1) Tomas Bjerre *2016-01-30 16:39:21*

Correcting revision logging

 * Resetting Jira and GitHub clients before tests. Was having troubles with the cache not being invalidated between tests.


## 1.20
### Other changes

[c3da07c0286855d](https://github.com/tomasbjerre/git-changelog-lib/commit/c3da07c0286855d) Tomas Bjerre *2016-01-30 11:56:15*

Including first commit

 * Was excluding it when ZERO_COMMIT constant was used

[0038494e3452b52](https://github.com/tomasbjerre/git-changelog-lib/commit/0038494e3452b52) Tomas Bjerre *2016-01-30 09:32:00*

Documentation

[96719f26ed22419](https://github.com/tomasbjerre/git-changelog-lib/commit/96719f26ed22419) Tomas Bjerre *2016-01-28 19:53:50*

Adding test cases to GitRepo

[7613efc4a5a0833](https://github.com/tomasbjerre/git-changelog-lib/commit/7613efc4a5a0833) Tomas Bjerre *2016-01-27 18:51:11*

Updating CHANGELOG.md

 * And Removing generate_changelog.sh


## 1.19
### GitHub [#11](https://github.com/tomasbjerre/git-changelog-lib/issues/11) Move command line to its own repo

[a7b20fdea564ff2](https://github.com/tomasbjerre/git-changelog-lib/commit/a7b20fdea564ff2) Tomas Bjerre *2016-01-27 18:14:33*

Removing command line code


### Other changes

[d405f4e8ae5f34f](https://github.com/tomasbjerre/git-changelog-lib/commit/d405f4e8ae5f34f) Tomas Bjerre *2015-12-11 07:56:23*

Changing Jenkins plugin link to point at JenkinsCI

[296d811a7ec3492](https://github.com/tomasbjerre/git-changelog-lib/commit/296d811a7ec3492) Tomas Bjerre *2015-12-10 17:12:44*

Adding example template to readme


## 1.18
### Other changes

[25fad813507296c](https://github.com/tomasbjerre/git-changelog-lib/commit/25fad813507296c) Tomas Bjerre *2015-12-08 18:44:25*

Downgrading JGIT to 3.6.2 to be compatible with its older API


## 1.17
### Other changes

[c7e3a39354cb339](https://github.com/tomasbjerre/git-changelog-lib/commit/c7e3a39354cb339) Tomas Bjerre *2015-12-05 14:30:21*

Allowing variables to be extended with custom context:s


## 1.16
### Other changes

[b818fd6a2f1207c](https://github.com/tomasbjerre/git-changelog-lib/commit/b818fd6a2f1207c) Tomas Bjerre *2015-12-05 14:01:56*

Peeling references

 * To support annotated tags


## 1.15
### Other changes

[2d0c1f1102387d0](https://github.com/tomasbjerre/git-changelog-lib/commit/2d0c1f1102387d0) Tomas Bjerre *2015-12-04 22:09:16*

Using JGit to find repo folder correctly

 * And JsonPath 2.1.0


## 1.14
### Other changes

[2b9b6112244d1aa](https://github.com/tomasbjerre/git-changelog-lib/commit/2b9b6112244d1aa) Tomas Bjerre *2015-12-01 21:34:52*

Caching requests to GitHub and Jira


## 1.13
### Other changes

[abe713cb069aa1f](https://github.com/tomasbjerre/git-changelog-lib/commit/abe713cb069aa1f) Tomas Bjerre *2015-11-23 21:44:23*

Avoiding crash if GitHub issue cant be found


## 1.12
### GitHub [#2](https://github.com/tomasbjerre/git-changelog-lib/issues/2) GitHub Integration

[45af766856ac703](https://github.com/tomasbjerre/git-changelog-lib/commit/45af766856ac703) Tomas Bjerre *2015-11-22 19:51:40*

Integrating with GitHub


### GitHub [#3](https://github.com/tomasbjerre/git-changelog-lib/issues/3) Jira Integration

[30a1095331ded15](https://github.com/tomasbjerre/git-changelog-lib/commit/30a1095331ded15) Tomas Bjerre *2015-11-23 17:32:23*

Integrating with Jira


## 1.11
### Other changes

[c655cbd914d06f3](https://github.com/tomasbjerre/git-changelog-lib/commit/c655cbd914d06f3) Tomas Bjerre *2015-11-21 15:36:03*

Changing master reference constan, to just master

 * refs/heads/master may not exist, perhaps its refs/remotes/origin/master

[8c95fd4870872d4](https://github.com/tomasbjerre/git-changelog-lib/commit/8c95fd4870872d4) Tomas Bjerre *2015-11-21 14:24:35*

doc


## 1.10
### Other changes

[c8da374a92183d2](https://github.com/tomasbjerre/git-changelog-lib/commit/c8da374a92183d2) Tomas Bjerre *2015-11-21 14:18:19*

Adding test cases for MediaWiki integration

 * Linking readme to screenshots
 * Not encoding html-tags for MediaWiki, to enable setting the toclimit with a tag.

[7749ddfa42b4034](https://github.com/tomasbjerre/git-changelog-lib/commit/7749ddfa42b4034) Tomas Bjerre *2015-11-20 21:38:13*

Adding example html-template


## 1.9
### Bugs #bug 

[b5f03710018a65d](https://github.com/tomasbjerre/git-changelog-lib/commit/b5f03710018a65d) Tomas Bjerre *2015-11-20 21:12:55*

Adding custom issues correctly fix


### Other changes

[d669ae9e7d00e52](https://github.com/tomasbjerre/git-changelog-lib/commit/d669ae9e7d00e52) Tomas Bjerre *2015-11-20 20:34:55*

Better regular expression for extracting readable part of tag name #feature


## 1.8
### Bugs #bug 

[c226865b64ce01c](https://github.com/tomasbjerre/git-changelog-lib/commit/c226865b64ce01c) Tomas Bjerre *2015-11-20 20:24:08*

Setting default setting of ignore commits regexp fix


## 1.7
### Bugs #bug 

[388a3a851f665c1](https://github.com/tomasbjerre/git-changelog-lib/commit/388a3a851f665c1) Tomas Bjerre *2015-11-20 19:08:45*

Using correct reference fix


## 1.6
### Other changes

[bfa6753630efcdb](https://github.com/tomasbjerre/git-changelog-lib/commit/bfa6753630efcdb) Tomas Bjerre *2015-11-20 17:03:04*

Some more work to get the lib working with Jenkins plugin

[f978c6fd9c72233](https://github.com/tomasbjerre/git-changelog-lib/commit/f978c6fd9c72233) Tomas Bjerre *2015-11-19 21:21:04*

Better error if not setting fromRepo


## 1.5
### Other changes

[d3e8656074af987](https://github.com/tomasbjerre/git-changelog-lib/commit/d3e8656074af987) Tomas Bjerre *2015-11-19 20:56:47*

Not loading default settings by default when using API

[64f318ca8f10ba8](https://github.com/tomasbjerre/git-changelog-lib/commit/64f318ca8f10ba8) Tomas Bjerre *2015-11-19 19:46:44*

updating changelog


## 1.4
### GitHub [#7](https://github.com/tomasbjerre/git-changelog-lib/issues/7) Add booleans to enable if statements

[fbb455921921ddc](https://github.com/tomasbjerre/git-changelog-lib/commit/fbb455921921ddc) Tomas Bjerre *2015-11-18 19:41:03*

Adding hasIssue hasLink to readme doc


### Other changes

[49c51f0fa73f35c](https://github.com/tomasbjerre/git-changelog-lib/commit/49c51f0fa73f35c) Tomas Bjerre *2015-11-19 19:40:27*

Making sure several custom issue patterns can be set with API

[1669b02acf8c9fb](https://github.com/tomasbjerre/git-changelog-lib/commit/1669b02acf8c9fb) Tomas Bjerre *2015-11-18 21:00:59*

Updating mediawiki screenshot


## 1.3
### GitHub [#7](https://github.com/tomasbjerre/git-changelog-lib/issues/7) Add booleans to enable if statements

[210e963a07b8a7b](https://github.com/tomasbjerre/git-changelog-lib/commit/210e963a07b8a7b) Tomas Bjerre *2015-11-18 19:06:00*

Adding booleans to check if link and/or issue exists in issue


### Other changes

[467d126c87b734e](https://github.com/tomasbjerre/git-changelog-lib/commit/467d126c87b734e) Tomas Bjerre *2015-11-18 19:17:48*

Adding new mediawiki screenshot

[49afd799253a777](https://github.com/tomasbjerre/git-changelog-lib/commit/49afd799253a777) Tomas Bjerre *2015-11-18 06:55:50*

Update README.md

[a083453437d20bd](https://github.com/tomasbjerre/git-changelog-lib/commit/a083453437d20bd) Tomas Bjerre *2015-11-17 20:29:42*

doc


## 1.2
### GitHub [#4](https://github.com/tomasbjerre/git-changelog-lib/issues/4) Mediawiki integration

[f45829b2c6a78d7](https://github.com/tomasbjerre/git-changelog-lib/commit/f45829b2c6a78d7) Tomas Bjerre *2015-11-17 19:27:03*

MediaWiki integration


### Other changes

[440ba52998293de](https://github.com/tomasbjerre/git-changelog-lib/commit/440ba52998293de) Tomas Bjerre *2015-11-15 15:08:56*

doc

[8fe5a0b66660e6d](https://github.com/tomasbjerre/git-changelog-lib/commit/8fe5a0b66660e6d) Tomas Bjerre *2015-11-15 14:39:41*

Adding link to Maven plugin

[47748d4350e9559](https://github.com/tomasbjerre/git-changelog-lib/commit/47748d4350e9559) Tomas Bjerre *2015-11-15 13:16:55*

Doc

[dd31f0a16fedfcf](https://github.com/tomasbjerre/git-changelog-lib/commit/dd31f0a16fedfcf) Tomas Bjerre *2015-11-15 12:47:06*

Changing regexp pattern for github

 * So that it does not match feature issues #feature

[a599d143f676b92](https://github.com/tomasbjerre/git-changelog-lib/commit/a599d143f676b92) Tomas Bjerre *2015-11-15 12:42:08*

Better error message if no template specified #feature

[6dd3418a6dc5b0d](https://github.com/tomasbjerre/git-changelog-lib/commit/6dd3418a6dc5b0d) Tomas Bjerre *2015-11-15 11:41:48*

Prepare for next release


## 1.1
### Bugs #bug 

[b3ddd5fdad30f41](https://github.com/tomasbjerre/git-changelog-lib/commit/b3ddd5fdad30f41) Tomas Bjerre *2015-11-15 09:48:35*

Adding generated CHANGELOG.md #feature

 * Correcting faulty precondition check, file output argument could not be set.

[82be7c398d445bd](https://github.com/tomasbjerre/git-changelog-lib/commit/82be7c398d445bd) Tomas Bjerre *2015-11-15 09:15:38*

Adding script to generate changelog

 * Finding git repo in parent folders correctly fix


### Other changes

[ec026ad2f25c612](https://github.com/tomasbjerre/git-changelog-lib/commit/ec026ad2f25c612) Tomas Bjerre *2015-11-15 11:37:18*

Car remove issue from commit message in changelog #feature

[3191a431aff2b20](https://github.com/tomasbjerre/git-changelog-lib/commit/3191a431aff2b20) Tomas Bjerre *2015-11-15 10:58:51*

Improving test output #feature

[59830c6f2a394fe](https://github.com/tomasbjerre/git-changelog-lib/commit/59830c6f2a394fe) Tomas Bjerre *2015-11-15 09:24:58*

Customizing changelog #feature

[ba9d565ddd15d1b](https://github.com/tomasbjerre/git-changelog-lib/commit/ba9d565ddd15d1b) Tomas Bjerre *2015-11-15 08:58:00*

More settings can be set from command line

 * More testing


## 1.0
### Other changes

[83a8a7b2f96ba88](https://github.com/tomasbjerre/git-changelog-lib/commit/83a8a7b2f96ba88) Tomas Bjerre *2015-11-14 19:36:21*

Doc

[5c54861708099d9](https://github.com/tomasbjerre/git-changelog-lib/commit/5c54861708099d9) Tomas Bjerre *2015-11-14 19:00:17*

Test

[051effe72405e78](https://github.com/tomasbjerre/git-changelog-lib/commit/051effe72405e78) Tomas Bjerre *2015-11-14 12:14:41*

Work for 1.0

[5aaeb907f68915a](https://github.com/tomasbjerre/git-changelog-lib/commit/5aaeb907f68915a) Tomas Bjerre *2015-11-14 09:46:23*

Initial commit

[a1aa5ff5b625e63](https://github.com/tomasbjerre/git-changelog-lib/commit/a1aa5ff5b625e63) Tomas Bjerre *2015-11-12 05:29:31*

Initial commit


