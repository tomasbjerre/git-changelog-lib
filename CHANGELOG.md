# Git Changelog changelog

Changelog of Git Changelog.

## Next release
### GitHub [#31](https://github.com/tomasbjerre/git-changelog-lib/pull/31) issue key was missing in issue link

**Fixing testcases after merge of**


[50dcd850d6bec5d](https://github.com/tomasbjerre/git-changelog-lib/commit/50dcd850d6bec5d) Tomas Bjerre *2016-08-11 14:14:57*


### Other changes

**issue key was missing in issue link**


[3abb1e05e90454d](https://github.com/tomasbjerre/git-changelog-lib/commit/3abb1e05e90454d) Ivan Korolev *2016-08-11 11:07:08*


## 1.55
### GitHub [#30](https://github.com/tomasbjerre/git-changelog-lib/issues/30) Getting full hash from Commit object

**Adding {{hashFull}} variable with full commit hash**


[7fd9971d4c2a30b](https://github.com/tomasbjerre/git-changelog-lib/commit/7fd9971d4c2a30b) Tomas Bjerre *2016-08-02 17:00:29*


## 1.54
### GitHub [#28](https://github.com/tomasbjerre/git-changelog-lib/issues/28) Crash when git repo has no master branch

**Allowing master branch to be absent**


[dd4633ba627695d](https://github.com/tomasbjerre/git-changelog-lib/commit/dd4633ba627695d) Tomas Bjerre *2016-06-27 17:26:30*


### Other changes

**More testing**


[2ae18a51778cb90](https://github.com/tomasbjerre/git-changelog-lib/commit/2ae18a51778cb90) Tomas Bjerre *2016-06-26 17:59:45*


## 1.53
### Other changes

**Faster**


[9580c7f449e576c](https://github.com/tomasbjerre/git-changelog-lib/commit/9580c7f449e576c) Tomas Bjerre *2016-06-24 23:59:39*


## 1.52
### GitHub [#29](https://github.com/tomasbjerre/git-changelog-lib/issues/29) Not, always, including merged in commits

**Correcting how to find diffing commits #**


[2589bd16e3d959b](https://github.com/tomasbjerre/git-changelog-lib/commit/2589bd16e3d959b) Tomas Bjerre *2016-06-24 15:46:01*

**Correcting how to find diffing commits**


[717602bef6a4ab1](https://github.com/tomasbjerre/git-changelog-lib/commit/717602bef6a4ab1) Tomas Bjerre *2016-06-24 15:45:29*


### Other changes

**Reverting a723e0a**


[47e35b1dc9dac85](https://github.com/tomasbjerre/git-changelog-lib/commit/47e35b1dc9dac85) Tomas Bjerre *2016-06-24 15:08:53*


## 1.51
### Other changes

**Show found refs when not finding ref**


[aef7ce6729a1b82](https://github.com/tomasbjerre/git-changelog-lib/commit/aef7ce6729a1b82) Tomas Bjerre *2016-06-24 12:26:00*

**Some more debugging**


[0c59757f08f5eb3](https://github.com/tomasbjerre/git-changelog-lib/commit/0c59757f08f5eb3) Tomas Bjerre *2016-06-24 09:18:35*


## 1.50
### GitHub [#29](https://github.com/tomasbjerre/git-changelog-lib/issues/29) Not, always, including merged in commits

**Including commits frmo merges**


[a723e0a230e3f38](https://github.com/tomasbjerre/git-changelog-lib/commit/a723e0a230e3f38) Tomas Bjerre *2016-06-24 08:56:47*


## 1.49
### GitHub [#28](https://github.com/tomasbjerre/git-changelog-lib/issues/28) Crash when git repo has no master branch

**Finding first commit in repo as parents of HEAD**

 * Was looking at parents of master, which may not exist. 

[87fe4044e587b1b](https://github.com/tomasbjerre/git-changelog-lib/commit/87fe4044e587b1b) Tomas Bjerre *2016-06-02 17:55:59*


### Other changes

**Adjusting example html template**


[7ea087fdbbc9820](https://github.com/tomasbjerre/git-changelog-lib/commit/7ea087fdbbc9820) Tomas Bjerre *2016-05-20 19:05:36*


## 1.48
### Other changes

**Testing trailing slash in Jira**


[0c85c4276ac3dce](https://github.com/tomasbjerre/git-changelog-lib/commit/0c85c4276ac3dce) Tomas Bjerre *2016-04-28 16:33:44*


## 1.47
### Other changes

**Removing trailing slash from Jira API URL**

 * If it is specified. 

[a94d576b6fd706e](https://github.com/tomasbjerre/git-changelog-lib/commit/a94d576b6fd706e) Tomas Bjerre *2016-04-28 16:18:20*


## 1.46
### Other changes

**Making API model serializable**


[0e0ce5e0c5df526](https://github.com/tomasbjerre/git-changelog-lib/commit/0e0ce5e0c5df526) Tomas Bjerre *2016-04-24 07:29:47*


## 1.45
### GitHub [#26](https://github.com/tomasbjerre/git-changelog-lib/issues/26) Excluded commits are included JENKINS-34156

**Including correct commits + performance**

 * Found major performance problem when sorting tags by commit time, fixed. 
 * Now not following parents, unless *from* is merged into them. 

[e3106df640b693b](https://github.com/tomasbjerre/git-changelog-lib/commit/e3106df640b693b) Tomas Bjerre *2016-04-13 18:30:26*


### Jira JENKINS-34156 

**Including correct commits + performance**

 * Found major performance problem when sorting tags by commit time, fixed. 
 * Now not following parents, unless *from* is merged into them. 

[e3106df640b693b](https://github.com/tomasbjerre/git-changelog-lib/commit/e3106df640b693b) Tomas Bjerre *2016-04-13 18:30:26*


## 1.44
### Jira JENKINS-34155 

**Support short SHA**


[27642e3db66e67c](https://github.com/tomasbjerre/git-changelog-lib/commit/27642e3db66e67c) Tomas Bjerre *2016-04-12 16:43:21*


## 1.43
### Other changes

**Parsing commits, oldest first**


[f6a768567dc03d3](https://github.com/tomasbjerre/git-changelog-lib/commit/f6a768567dc03d3) Tomas Bjerre *2016-04-10 09:08:26*


## 1.42
### GitHub [#23](https://github.com/tomasbjerre/git-changelog-lib/issues/23) changelog is generating incorrect order of commits/Issues

**Parsing commits, oldest first**

 * To avoid random behaviour. 

[9817cdf6a1fbe91](https://github.com/tomasbjerre/git-changelog-lib/commit/9817cdf6a1fbe91) Tomas Bjerre *2016-04-10 08:49:45*


## 1.41
### GitHub [#23](https://github.com/tomasbjerre/git-changelog-lib/issues/23) changelog is generating incorrect order of commits/Issues

**Traversing commit tree by parents**

 * To find all commits in all tags. 

[9dcba5d33fc2e44](https://github.com/tomasbjerre/git-changelog-lib/commit/9dcba5d33fc2e44) Tomas Bjerre *2016-04-09 20:21:58*


## 1.40
### GitHub [#23](https://github.com/tomasbjerre/git-changelog-lib/issues/23) changelog is generating incorrect order of commits/Issues

**Adding feature to ignore tags by regexp**

 * Also testing tag in feature branch 

[8166ec7dfd456df](https://github.com/tomasbjerre/git-changelog-lib/commit/8166ec7dfd456df) Tomas Bjerre *2016-04-06 20:39:27*


### Other changes

**Updating CHANGELOG.md**


[96ee3bb55e4da2d](https://github.com/tomasbjerre/git-changelog-lib/commit/96ee3bb55e4da2d) Tomas Bjerre *2016-03-20 13:25:18*


## 1.39
### Other changes

**Sorting filtered commits, was random from hash**

 * Building with OpenJDK7 in Travis 

[b976129fb928dc1](https://github.com/tomasbjerre/git-changelog-lib/commit/b976129fb928dc1) Tomas Bjerre *2016-03-20 09:42:23*


## 1.38
### GitHub [#19](https://github.com/tomasbjerre/git-changelog-lib/issues/19) Feature-request (or question): Issues by category

**Removing commits without issue, from tags**


[22a2c55c44b6b4d](https://github.com/tomasbjerre/git-changelog-lib/commit/22a2c55c44b6b4d) Tomas Bjerre *2016-03-20 08:38:27*


## 1.37
### GitHub [#19](https://github.com/tomasbjerre/git-changelog-lib/issues/19) Feature-request (or question): Issues by category

**Ignore commits without issue**


[0b70db9921da8dd](https://github.com/tomasbjerre/git-changelog-lib/commit/0b70db9921da8dd) Tomas Bjerre *2016-03-19 22:53:07*

**Adding issueTypes in context**


[a025adfc759313f](https://github.com/tomasbjerre/git-changelog-lib/commit/a025adfc759313f) Tomas Bjerre *2016-03-19 20:33:11*


### GitHub [#21](https://github.com/tomasbjerre/git-changelog-lib/issues/21) Commits can be listed multiple times

**Avoiding adding commit twice in same issue**


[414348773e3d658](https://github.com/tomasbjerre/git-changelog-lib/commit/414348773e3d658) Tomas Bjerre *2016-03-19 22:16:40*


### Other changes

**Avoiding parsing commits twice**


[cb23e700107d5c1](https://github.com/tomasbjerre/git-changelog-lib/commit/cb23e700107d5c1) Tomas Bjerre *2016-03-20 06:52:21*

**Updating test cases after changing test-branch**


[ca6ee75dc629894](https://github.com/tomasbjerre/git-changelog-lib/commit/ca6ee75dc629894) Tomas Bjerre *2016-03-19 20:56:45*


## 1.36
### Other changes

**Fixing infinite loop in GitRepo**

 * Found in 1.19 
 * at se.bjurr.gitchangelog.internal.git.GitRepo.toString(GitRepo.java:208) 
 * at se.bjurr.gitchangelog.internal.git.GitRepo.getRef(GitRepo.java:174) 
 * at se.bjurr.gitchangelog.internal.git.GitRepo.firstCommit(GitRepo.java:195) 
 * at se.bjurr.gitchangelog.internal.git.GitRepo.toString(GitRepo.java:208) 
 * at se.bjurr.gitchangelog.internal.git.GitRepo.getRef(GitRepo.java:174) 

[a4a15094f367c31](https://github.com/tomasbjerre/git-changelog-lib/commit/a4a15094f367c31) Tomas Bjerre *2016-03-15 20:27:41*


## 1.35
### Other changes

**Using okhttp 2.7.5 was using 2.3.0**

 * Which caused ClassNotFoundException for okio/ForwardingTimeout. 

[a4bb6103886b293](https://github.com/tomasbjerre/git-changelog-lib/commit/a4bb6103886b293) Tomas Bjerre *2016-03-15 20:12:47*


## 1.34
### GitHub [#18](https://github.com/tomasbjerre/git-changelog-lib/pull/18) Migrate GitHub to RetroFit , add pagination and token support

**Logging error if error invoking GitHub API**


[a040e51847b4f23](https://github.com/tomasbjerre/git-changelog-lib/commit/a040e51847b4f23) Tomas Bjerre *2016-03-15 19:05:58*


### Other changes

**Updating CHANGELOG.md**


[2cf820ce81c7b15](https://github.com/tomasbjerre/git-changelog-lib/commit/2cf820ce81c7b15) Tomas Bjerre *2016-03-15 17:52:18*


## 1.33
### GitHub [#10](https://github.com/tomasbjerre/git-changelog-lib/issues/10) Authentication with GitHub

**Migrate GitHub REST-API to RetroFit library**

 * Added auth-token support for GitHub, fixes 
 * Added pagination support for GitHub, fixes 

[d29029a38fad6a4](https://github.com/tomasbjerre/git-changelog-lib/commit/d29029a38fad6a4) Jonas Kalderstam *2016-03-15 00:12:45*


### GitHub [#15](https://github.com/tomasbjerre/git-changelog-lib/issues/15) Github, support pagination 

**Migrate GitHub REST-API to RetroFit library**

 * Added auth-token support for GitHub, fixes 
 * Added pagination support for GitHub, fixes 

[d29029a38fad6a4](https://github.com/tomasbjerre/git-changelog-lib/commit/d29029a38fad6a4) Jonas Kalderstam *2016-03-15 00:12:45*


### GitHub [#18](https://github.com/tomasbjerre/git-changelog-lib/pull/18) Migrate GitHub to RetroFit , add pagination and token support

**Introducing custom exceptions**

 * Also fixing some issues from PR . Removing duplicate Gson, System.out, throwing exceptions. 

[4cb3f757f8a6b9f](https://github.com/tomasbjerre/git-changelog-lib/commit/4cb3f757f8a6b9f) Tomas Bjerre *2016-03-15 17:22:33*


### Other changes

**Update README.md**


[e37195ae3a5bb32](https://github.com/tomasbjerre/git-changelog-lib/commit/e37195ae3a5bb32) Tomas Bjerre *2016-02-22 16:28:48*


## 1.32
### GitHub [#16](https://github.com/tomasbjerre/git-changelog-lib/issues/16) Commit not available in all issues mentioned in commit comment

**Supplying commit in each issue mentioned in message**


[102431686668a6c](https://github.com/tomasbjerre/git-changelog-lib/commit/102431686668a6c) Tomas Bjerre *2016-02-19 22:18:39*


### Other changes

**Avoiding unnecessary Optional in JiraClient**


[feeb61cc1f7ebd3](https://github.com/tomasbjerre/git-changelog-lib/commit/feeb61cc1f7ebd3) Tomas Bjerre *2016-02-19 19:20:56*


## 1.31
### Other changes

**Enabling custom Jira client**


[b078d04fbfb978f](https://github.com/tomasbjerre/git-changelog-lib/commit/b078d04fbfb978f) Tomas Bjerre *2016-02-19 18:55:55*


## 1.30
### Other changes

**Bugfix, handling multiple tags on same commit**

 * Using the last one found. 
 * Also refactoring tests. Added a special test-branch to use real GIT instead of fake repo. 

[3c794133dcc1d00](https://github.com/tomasbjerre/git-changelog-lib/commit/3c794133dcc1d00) Tomas Bjerre *2016-02-15 17:41:45*


## 1.29
### Other changes

**Bugfix, crashed if all commits in tag were ignored**


[be4143904d6382c](https://github.com/tomasbjerre/git-changelog-lib/commit/be4143904d6382c) Tomas Bjerre *2016-02-14 17:33:46*


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
### Bugs #bug Mixed bugs

**Adding custom issues correctly fix**


[b5f03710018a65d](https://github.com/tomasbjerre/git-changelog-lib/commit/b5f03710018a65d) Tomas Bjerre *2015-11-20 21:12:55*


### Other changes

**Better regular expression for extracting readable part of tag name #feature**


[d669ae9e7d00e52](https://github.com/tomasbjerre/git-changelog-lib/commit/d669ae9e7d00e52) Tomas Bjerre *2015-11-20 20:34:55*


## 1.8
### Bugs #bug Mixed bugs

**Setting default setting of ignore commits regexp fix**


[c226865b64ce01c](https://github.com/tomasbjerre/git-changelog-lib/commit/c226865b64ce01c) Tomas Bjerre *2015-11-20 20:24:08*


## 1.7
### Bugs #bug Mixed bugs

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
### Bugs #bug Mixed bugs

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


