
# git-changelog-lib changelog

Changelog of git-changelog-lib.

## 1.144.4
### No issue

**using new build script**


[82726fdb6608dca](https://github.com/tomasbjerre/git-changelog-lib/commit/82726fdb6608dca) Tomas Bjerre *2021-04-02 18:37:49*


## 1.96
### Jira JENKINS-65252   

**pretty printing output**


[bea18ab90db148c](https://github.com/tomasbjerre/git-changelog-lib/commit/bea18ab90db148c) Tomas Bjerre *2021-03-29 15:33:00*


## 1.95
### No issue

**Removing default ignore filter on message**

* It was: &#x60;^\[maven-release-plugin\].*|^\[Gradle Release Plugin\].*|^Merge.*&quot;&#x60; 
* But users are confused by this and it is probably better to have no filter by default. 

[a57e321f08cea10](https://github.com/tomasbjerre/git-changelog-lib/commit/a57e321f08cea10) Tomas Bjerre *2021-01-18 16:46:13*

**Switching to Handlebars from Mustache**

* Removing Mediawiki support. Because I have not been able to make it work. https://stackoverflow.com/questions/45779754/cannot-authenticate-with-mediawiki-1-28-api 
* Steeping up JGit to latest release. 3.6.2.201501210735-r to 5.10.0.202012080955-r. 
* Refactoring tests to use Approvals. 

[7eba3d038b884b8](https://github.com/tomasbjerre/git-changelog-lib/commit/7eba3d038b884b8) Tomas Bjerre *2020-12-24 07:11:41*


## 1.94
### GitHub #94   

**Adjustments after merge of PR**


[6e07d0b76da227e](https://github.com/tomasbjerre/git-changelog-lib/commit/6e07d0b76da227e) Tomas Bjerre *2020-11-18 17:14:10*


### No issue

**Add an option to filter the commits using a path filter string**

* This works exactly like &#x60;git log -- path&#x60; 

[94df887471f9f97](https://github.com/tomasbjerre/git-changelog-lib/commit/94df887471f9f97) Gabriel Einsdorf *2020-11-18 13:48:10*


## 1.93
### No issue

**Support custom headers to JIRA to bypass 2 factor auth**


[c43070c5cd20162](https://github.com/tomasbjerre/git-changelog-lib/commit/c43070c5cd20162) Yauheni Biruk *2020-11-07 13:53:10*


## 1.92
### GitHub #78   

**naming in api tomasbjerre/git-changelog-lib**


[21ef6027c82814e](https://github.com/tomasbjerre/git-changelog-lib/commit/21ef6027c82814e) Tomas Bjerre *2020-02-20 16:31:35*


### No issue

**add support for jira tokens**


[fc75402ade75684](https://github.com/tomasbjerre/git-changelog-lib/commit/fc75402ade75684) Dmitry Mamchur *2020-02-20 15:43:30*

**Spotbugs**


[3149f34c0b2a8e5](https://github.com/tomasbjerre/git-changelog-lib/commit/3149f34c0b2a8e5) Tomas Bjerre *2019-10-08 19:39:27*

**Create FUNDING.yml**


[f00b0e46124db77](https://github.com/tomasbjerre/git-changelog-lib/commit/f00b0e46124db77) Tomas Bjerre *2019-09-28 07:06:44*


## 1.91
### No issue

**Removing dependency on javax.xml**


[6515299f938c982](https://github.com/tomasbjerre/git-changelog-lib/commit/6515299f938c982) Tomas Bjerre *2019-06-11 16:54:48*


## 1.90
### GitHub #73   

**Clearer exception stacktrace**


[0a8d3dc6aa79b1d](https://github.com/tomasbjerre/git-changelog-lib/commit/0a8d3dc6aa79b1d) Tomas Bjerre *2019-05-17 05:03:23*


### No issue

**JDK 11 compatible**


[cd9162d9035e450](https://github.com/tomasbjerre/git-changelog-lib/commit/cd9162d9035e450) Tomas Bjerre *2019-06-11 16:21:57*

**OpenJDK 11 in Travis**


[ba751250bef78f1](https://github.com/tomasbjerre/git-changelog-lib/commit/ba751250bef78f1) Tomas Bjerre *2019-06-11 16:15:19*


## 1.89
### No issue

**Avoiding global state in JiraClientFactory**


[1aa1c4ca13d06a9](https://github.com/tomasbjerre/git-changelog-lib/commit/1aa1c4ca13d06a9) Tomas Bjerre *2019-02-16 17:08:53*


## 1.88
### No issue

**Setting version to fix faulty release**


[a562becc5f40a2f](https://github.com/tomasbjerre/git-changelog-lib/commit/a562becc5f40a2f) Tomas Bjerre *2019-01-08 17:02:29*

**Setting version to fix faulty release**


[10545b0bd17a01f](https://github.com/tomasbjerre/git-changelog-lib/commit/10545b0bd17a01f) Tomas Bjerre *2019-01-08 16:46:06*

**update README**


[7a0565cf32c0504](https://github.com/tomasbjerre/git-changelog-lib/commit/7a0565cf32c0504) Lukas Läderach *2019-01-07 21:16:05*

**add unit tests for jira linked issues**


[c7e0aa691c5f26b](https://github.com/tomasbjerre/git-changelog-lib/commit/c7e0aa691c5f26b) Lukas Läderach *2019-01-07 21:03:00*

**Add support for LinkedIssues (inward, outward) in Jira (caused by, references)**


[794eb3af4173fdc](https://github.com/tomasbjerre/git-changelog-lib/commit/794eb3af4173fdc) Lukas Läderach *2018-12-13 09:41:39*


## 1.85
### GitHub #70   

**Correcting GitLab integration after**


[7d1e69bf235452e](https://github.com/tomasbjerre/git-changelog-lib/commit/7d1e69bf235452e) Tomas Bjerre *2018-10-27 17:48:50*


## 1.84
### GitHub #68   

**Testing issue types**


[d812191c55684be](https://github.com/tomasbjerre/git-changelog-lib/commit/d812191c55684be) Tomas Bjerre *2018-10-27 10:32:47*


### No issue

**upgrade gitlab dependency**

* Gitlab v3 has been deprecated (see https://about.gitlab.com/2018/06/01/api-v3-removal-impending/) and a new version was released. 

[f125a10ae48f178](https://github.com/tomasbjerre/git-changelog-lib/commit/f125a10ae48f178) Jorge *2018-10-27 16:14:52*

**Moving some test cases**


[055401cd6153ef1](https://github.com/tomasbjerre/git-changelog-lib/commit/055401cd6153ef1) Tomas Bjerre *2018-10-27 14:56:58*


## 1.83
### GitHub #68   

**Adding booleans isJira, isGitHub...**


[44eff0421308787](https://github.com/tomasbjerre/git-changelog-lib/commit/44eff0421308787) Tomas Bjerre *2018-10-27 05:36:13*


### No issue

**Doc - correct typo in authorEmail**


[f06cc266d41e4d6](https://github.com/tomasbjerre/git-changelog-lib/commit/f06cc266d41e4d6) Beth Skurrie *2018-10-09 01:21:11*


## 1.82
### No issue

**Avoiding NPE:s and some refactoring**


[07a0e34440540b6](https://github.com/tomasbjerre/git-changelog-lib/commit/07a0e34440540b6) Tomas Bjerre *2018-09-13 06:54:32*


## 1.81
### No issue

**Correcting ownerName when dot in repo name**


[56f23fe9eff5487](https://github.com/tomasbjerre/git-changelog-lib/commit/56f23fe9eff5487) Tomas Bjerre *2018-09-12 14:41:21*

**Trimming from/to refs**


[b594b083087c90c](https://github.com/tomasbjerre/git-changelog-lib/commit/b594b083087c90c) Tomas Bjerre *2018-07-16 11:54:58*


## 1.80
### No issue

**Closing connection to Jira**


[d1bbba0172779da](https://github.com/tomasbjerre/git-changelog-lib/commit/d1bbba0172779da) Tomas Bjerre *2018-05-05 07:42:30*

**Doc**


[fd5990de3e69905](https://github.com/tomasbjerre/git-changelog-lib/commit/fd5990de3e69905) Tomas Bjerre *2018-04-20 15:15:48*


## 1.79
### No issue

**Bumping version to fix faulty release**


[df186edf843a598](https://github.com/tomasbjerre/git-changelog-lib/commit/df186edf843a598) Tomas Bjerre *2018-01-09 20:53:41*

**Removing state from GitHub Client**

* The client was created once, with one API, and kept for all future invcations. So that if a changelog was created for one repo (A) and then for a repo (B), then B would use the API from A. Resulting in wrong issue information in B. 

[8ef76f5c6653ab3](https://github.com/tomasbjerre/git-changelog-lib/commit/8ef76f5c6653ab3) Tomas Bjerre *2018-01-09 20:46:33*

**changelog.json: Fix invalid JSON**


[46d37d70e2f9ffe](https://github.com/tomasbjerre/git-changelog-lib/commit/46d37d70e2f9ffe) Chad Horohoe *2018-01-03 02:31:13*


## 1.77
### Jira JENKINS-19994   

**Closing RevWalk**


[9f7c782dc9be919](https://github.com/tomasbjerre/git-changelog-lib/commit/9f7c782dc9be919) Tomas Bjerre *2017-12-30 20:07:57*


### No issue

**Using UTF-8, instead of default**


[7fe5f6b9860efe7](https://github.com/tomasbjerre/git-changelog-lib/commit/7fe5f6b9860efe7) Tomas Bjerre *2017-12-24 21:59:34*


## 1.76
### GitHub #59   

**Updating Doc after merge of**


[cef15b8443eacef](https://github.com/tomasbjerre/git-changelog-lib/commit/cef15b8443eacef) Tomas Bjerre *2017-12-19 10:43:45*


### No issue

**README updated**


[77ceccd935bc8cd](https://github.com/tomasbjerre/git-changelog-lib/commit/77ceccd935bc8cd) Michael Hauck *2017-12-18 14:18:26*

**Fixing Model and Parser**


[761f40fd6237226](https://github.com/tomasbjerre/git-changelog-lib/commit/761f40fd6237226) Michael Hauck *2017-12-18 14:01:38*

**Adding support for Jira Issue Description**


[6472aa221cc6e2d](https://github.com/tomasbjerre/git-changelog-lib/commit/6472aa221cc6e2d) Michael Hauck *2017-12-18 12:21:11*

**Doc**


[42e73bdf05c384a](https://github.com/tomasbjerre/git-changelog-lib/commit/42e73bdf05c384a) Tomas Bjerre *2017-12-03 19:47:23*


## 1.75
### No issue

**Using shared build scripts**


[6c906db94a069a7](https://github.com/tomasbjerre/git-changelog-lib/commit/6c906db94a069a7) Tomas Bjerre *2017-12-03 07:45:37*

**Doc**


[74fd3ae5e58aa76](https://github.com/tomasbjerre/git-changelog-lib/commit/74fd3ae5e58aa76) Tomas Bjerre *2017-11-18 12:14:20*


## 1.74
### GitHub #58   

**Avoid fetching from integrations if not used**

* Not fetching information from integrations (GitHub, GitLab, Jira) if that information is not used in the template. 

[e87efa3246489c2](https://github.com/tomasbjerre/git-changelog-lib/commit/e87efa3246489c2) Tomas Bjerre *2017-11-18 11:26:24*


### No issue

**Updating build tools and removing shaddow jar**


[57dc01ac9223fd7](https://github.com/tomasbjerre/git-changelog-lib/commit/57dc01ac9223fd7) Tomas Bjerre *2017-11-18 12:00:17*


## 1.73
### No issue

**Avoiding usage of Guava Objects**

* Because it results in NoSuchMethodException when newer Guava version exists on classpath. Where Objects is replaced with MoreObjects. 

[94dc5eee25f9414](https://github.com/tomasbjerre/git-changelog-lib/commit/94dc5eee25f9414) Tomas Bjerre *2017-11-02 17:06:49*


## 1.72
### GitHub #51   

**Rewrite MediaWiki Client for Botuser**


[9cd8d1dfa42aea1](https://github.com/tomasbjerre/git-changelog-lib/commit/9cd8d1dfa42aea1) Tomas Bjerre *2017-08-31 17:23:58*


### No issue

**Disabling MediaWiki integration tests**


[b82e25da5b3d681](https://github.com/tomasbjerre/git-changelog-lib/commit/b82e25da5b3d681) Tomas Bjerre *2017-09-03 09:15:28*

**Travis with JDK8**


[db79d0fb4f7a33d](https://github.com/tomasbjerre/git-changelog-lib/commit/db79d0fb4f7a33d) Tomas Bjerre *2017-09-01 19:25:13*


## 1.71
### No issue

**Correcting owner/repo name cloneUrl without dot git**


[eac21b71186099b](https://github.com/tomasbjerre/git-changelog-lib/commit/eac21b71186099b) Tomas Bjerre *2017-07-25 18:21:50*


## 1.70
### GitHub #49   

**Gathering repo provider information**

* Getting ownerName and repoName from clone URL. 
* Setting GitLab server and GitHub API from clone URL. 

[dd8497034624b12](https://github.com/tomasbjerre/git-changelog-lib/commit/dd8497034624b12) Tomas Bjerre *2017-07-23 19:10:43*


### No issue

**Cleaning**


[3c0bf2f7885bff3](https://github.com/tomasbjerre/git-changelog-lib/commit/3c0bf2f7885bff3) Tomas Bjerre *2017-07-16 05:55:55*


## 1.69
### GitHub #47   

**Adjustments after merge of**

* Using the Date data type instead of String to supply ignoreCommitsOlderThan. 
* Making ignoreCommits more effective. 
* Also cleaning up unrelated parts of the API. Use File instead of String to supply changelog file. Enabling write changelog to Writer. 

[121e65493ec764b](https://github.com/tomasbjerre/git-changelog-lib/commit/121e65493ec764b) Tomas Bjerre *2017-07-07 20:04:00*


### No issue

**Fix CommitsWithMesssage typo -> CommitsWithMessage**


[67667ba3a5258fd](https://github.com/tomasbjerre/git-changelog-lib/commit/67667ba3a5258fd) jakob *2017-07-04 00:16:47*

**Add ignoreCommitsOlderThan date limiting**

* The rationale is that perhaps projects might: 
* use a git-based changelog to inform coworkers, rather than 
* customers, making a more short-lived &quot;news&quot;-style log desirable, 
* not (yet?) use frequent-enough tags in their repository, 
* want to provide a sense for the liveliness of a project 

[671816d3dc3d60a](https://github.com/tomasbjerre/git-changelog-lib/commit/671816d3dc3d60a) jakob *2017-07-03 23:53:47*

**Fix typo in readableTagName javadoc**


[6310062f7a0470c](https://github.com/tomasbjerre/git-changelog-lib/commit/6310062f7a0470c) jakob *2017-05-31 18:45:11*


## 1.68
### No issue

**doc**


[92fca3cfbb690ae](https://github.com/tomasbjerre/git-changelog-lib/commit/92fca3cfbb690ae) Tomas Bjerre *2017-04-14 09:08:25*

**tag time added to tag model**


[8c5837402c4a802](https://github.com/tomasbjerre/git-changelog-lib/commit/8c5837402c4a802) Alik Kurdyukov *2017-04-11 18:33:29*


## 1.67
### GitHub #2   

**GitLab integration**

* Adding it to the API. 

[743ab5566d39083](https://github.com/tomasbjerre/git-changelog-lib/commit/743ab5566d39083) Tomas Bjerre *2017-03-25 16:31:51*


## 1.66
### GitHub #42   

**GitLab integration**


[eb3d1ee8a5ed370](https://github.com/tomasbjerre/git-changelog-lib/commit/eb3d1ee8a5ed370) Tomas Bjerre *2017-03-25 15:06:44*


## 1.65
### No issue

**doc**


[9b2d45154695881](https://github.com/tomasbjerre/git-changelog-lib/commit/9b2d45154695881) Tomas Bjerre *2017-03-20 18:14:10*

**fix jira labels**


[dbf309010fbcd76](https://github.com/tomasbjerre/git-changelog-lib/commit/dbf309010fbcd76) Heorhi Bisiaryn *2017-03-20 15:23:57*


## 1.64
### GitHub #40   

**Adding issueType and labels attributes**


[6db44d44152f5da](https://github.com/tomasbjerre/git-changelog-lib/commit/6db44d44152f5da) Tomas Bjerre *2017-03-18 09:10:13*


### No issue

**Google java code standard**


[b4317dd44e3ddcc](https://github.com/tomasbjerre/git-changelog-lib/commit/b4317dd44e3ddcc) Tomas Bjerre *2017-03-17 16:22:01*

**jira issue type feature**


[fdd7229f9b2f183](https://github.com/tomasbjerre/git-changelog-lib/commit/fdd7229f9b2f183) Heorhi Bisiaryn *2017-03-15 11:54:01*


## 1.63
### No issue

**Adding timeout, 10 seconds**


[1c3ca2cf2d1e598](https://github.com/tomasbjerre/git-changelog-lib/commit/1c3ca2cf2d1e598) Tomas Bjerre *2017-03-01 18:13:14*

**doc**


[4de5c284efe8c52](https://github.com/tomasbjerre/git-changelog-lib/commit/4de5c284efe8c52) Tomas Bjerre *2017-02-19 07:21:42*

**Set theme jekyll-theme-slate**


[e569b0c8dfe5957](https://github.com/tomasbjerre/git-changelog-lib/commit/e569b0c8dfe5957) Tomas Bjerre *2017-01-12 03:04:46*

**Adding HTML example supplied by Joel Eriksson**

* Also moving example templates to examples folder. 

[4c554557c16f68d](https://github.com/tomasbjerre/git-changelog-lib/commit/4c554557c16f68d) Tomas Bjerre *2016-12-21 16:29:21*


## 1.62
### No issue

**mergeServiceFiles in fat jar**


[8b2fb9b3219ba47](https://github.com/tomasbjerre/git-changelog-lib/commit/8b2fb9b3219ba47) Tomas Bjerre *2016-12-17 14:29:25*


## 1.61
### GitHub #38   

**Relocate packages to avoid classpath issues**


[79f56692487b731](https://github.com/tomasbjerre/git-changelog-lib/commit/79f56692487b731) Tomas Bjerre *2016-12-16 20:02:28*


## 1.60
### GitHub #38   

**Relocate packages to avoid classpath issues**


[8d2aac7c6c174f1](https://github.com/tomasbjerre/git-changelog-lib/commit/8d2aac7c6c174f1) Tomas Bjerre *2016-12-16 16:35:06*


## 1.59
### GitHub #38   

**Relocate packages to avoid classpath issues**


[653720603542acb](https://github.com/tomasbjerre/git-changelog-lib/commit/653720603542acb) Tomas Bjerre *2016-12-16 16:26:45*


## 1.58
### GitHub #36   

**Adding annotation to context of tag**


[ecd852cfa40b0a7](https://github.com/tomasbjerre/git-changelog-lib/commit/ecd852cfa40b0a7) Tomas Bjerre *2016-10-22 07:23:29*


## 1.57
### GitHub #35   

**Adding merge boolean to commits**


[2d9189f421254f2](https://github.com/tomasbjerre/git-changelog-lib/commit/2d9189f421254f2) Tomas Bjerre *2016-10-05 17:04:13*


## 1.56
### GitHub #31   

**Fixing testcases after merge of**


[50dcd850d6bec5d](https://github.com/tomasbjerre/git-changelog-lib/commit/50dcd850d6bec5d) Tomas Bjerre *2016-08-11 14:14:57*


### No issue

**issue key was missing in issue link**


[3abb1e05e90454d](https://github.com/tomasbjerre/git-changelog-lib/commit/3abb1e05e90454d) Ivan Korolev *2016-08-11 11:07:08*


## 1.55
### GitHub #30   

**Adding {{hashFull}} variable with full commit hash**


[7fd9971d4c2a30b](https://github.com/tomasbjerre/git-changelog-lib/commit/7fd9971d4c2a30b) Tomas Bjerre *2016-08-02 17:00:29*


## 1.54
### GitHub #28   

**Allowing master branch to be absent**


[dd4633ba627695d](https://github.com/tomasbjerre/git-changelog-lib/commit/dd4633ba627695d) Tomas Bjerre *2016-06-27 17:26:30*


### No issue

**More testing**


[2ae18a51778cb90](https://github.com/tomasbjerre/git-changelog-lib/commit/2ae18a51778cb90) Tomas Bjerre *2016-06-26 17:59:45*


## 1.53
### No issue

**Faster**


[9580c7f449e576c](https://github.com/tomasbjerre/git-changelog-lib/commit/9580c7f449e576c) Tomas Bjerre *2016-06-24 23:59:39*


## 1.52
### GitHub #29   

**Correcting how to find diffing commits #**


[2589bd16e3d959b](https://github.com/tomasbjerre/git-changelog-lib/commit/2589bd16e3d959b) Tomas Bjerre *2016-06-24 15:46:01*

**Correcting how to find diffing commits**


[717602bef6a4ab1](https://github.com/tomasbjerre/git-changelog-lib/commit/717602bef6a4ab1) Tomas Bjerre *2016-06-24 15:45:29*


### No issue

**Reverting a723e0a**


[47e35b1dc9dac85](https://github.com/tomasbjerre/git-changelog-lib/commit/47e35b1dc9dac85) Tomas Bjerre *2016-06-24 15:08:53*


## 1.51
### No issue

**Show found refs when not finding ref**


[aef7ce6729a1b82](https://github.com/tomasbjerre/git-changelog-lib/commit/aef7ce6729a1b82) Tomas Bjerre *2016-06-24 12:26:00*

**Some more debugging**


[0c59757f08f5eb3](https://github.com/tomasbjerre/git-changelog-lib/commit/0c59757f08f5eb3) Tomas Bjerre *2016-06-24 09:18:35*


## 1.50
### GitHub #29   

**Including commits frmo merges**


[a723e0a230e3f38](https://github.com/tomasbjerre/git-changelog-lib/commit/a723e0a230e3f38) Tomas Bjerre *2016-06-24 08:56:47*


## 1.49
### GitHub #28   

**Finding first commit in repo as parents of HEAD**

* Was looking at parents of master, which may not exist. 

[87fe4044e587b1b](https://github.com/tomasbjerre/git-changelog-lib/commit/87fe4044e587b1b) Tomas Bjerre *2016-06-02 17:55:59*


### No issue

**Adjusting example html template**


[7ea087fdbbc9820](https://github.com/tomasbjerre/git-changelog-lib/commit/7ea087fdbbc9820) Tomas Bjerre *2016-05-20 19:05:36*


## 1.48
### No issue

**Testing trailing slash in Jira**


[0c85c4276ac3dce](https://github.com/tomasbjerre/git-changelog-lib/commit/0c85c4276ac3dce) Tomas Bjerre *2016-04-28 16:33:44*


## 1.47
### No issue

**Removing trailing slash from Jira API URL**

* If it is specified. 

[a94d576b6fd706e](https://github.com/tomasbjerre/git-changelog-lib/commit/a94d576b6fd706e) Tomas Bjerre *2016-04-28 16:18:20*


## 1.46
### No issue

**Making API model serializable**


[0e0ce5e0c5df526](https://github.com/tomasbjerre/git-changelog-lib/commit/0e0ce5e0c5df526) Tomas Bjerre *2016-04-24 07:29:47*


## 1.45
### GitHub #26   

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
### No issue

**Parsing commits, oldest first**


[f6a768567dc03d3](https://github.com/tomasbjerre/git-changelog-lib/commit/f6a768567dc03d3) Tomas Bjerre *2016-04-10 09:08:26*


## 1.42
### GitHub #23   

**Parsing commits, oldest first**

* To avoid random behaviour. 

[9817cdf6a1fbe91](https://github.com/tomasbjerre/git-changelog-lib/commit/9817cdf6a1fbe91) Tomas Bjerre *2016-04-10 08:49:45*


## 1.41
### GitHub #23   

**Traversing commit tree by parents**

* To find all commits in all tags. 

[9dcba5d33fc2e44](https://github.com/tomasbjerre/git-changelog-lib/commit/9dcba5d33fc2e44) Tomas Bjerre *2016-04-09 20:21:58*


## 1.40
### GitHub #23   

**Adding feature to ignore tags by regexp**

* Also testing tag in feature branch 

[8166ec7dfd456df](https://github.com/tomasbjerre/git-changelog-lib/commit/8166ec7dfd456df) Tomas Bjerre *2016-04-06 20:39:27*


### No issue

**Updating CHANGELOG.md**


[96ee3bb55e4da2d](https://github.com/tomasbjerre/git-changelog-lib/commit/96ee3bb55e4da2d) Tomas Bjerre *2016-03-20 13:25:18*


## 1.39
### No issue

**Sorting filtered commits, was random from hash**

* Building with OpenJDK7 in Travis 

[b976129fb928dc1](https://github.com/tomasbjerre/git-changelog-lib/commit/b976129fb928dc1) Tomas Bjerre *2016-03-20 09:42:23*


## 1.38
### GitHub #19   

**Removing commits without issue, from tags**


[22a2c55c44b6b4d](https://github.com/tomasbjerre/git-changelog-lib/commit/22a2c55c44b6b4d) Tomas Bjerre *2016-03-20 08:38:27*


## 1.37
### GitHub #19   

**Ignore commits without issue**


[0b70db9921da8dd](https://github.com/tomasbjerre/git-changelog-lib/commit/0b70db9921da8dd) Tomas Bjerre *2016-03-19 22:53:07*

**Adding issueTypes in context**


[a025adfc759313f](https://github.com/tomasbjerre/git-changelog-lib/commit/a025adfc759313f) Tomas Bjerre *2016-03-19 20:33:11*


### GitHub #21   

**Avoiding adding commit twice in same issue**


[414348773e3d658](https://github.com/tomasbjerre/git-changelog-lib/commit/414348773e3d658) Tomas Bjerre *2016-03-19 22:16:40*


### No issue

**Avoiding parsing commits twice**


[cb23e700107d5c1](https://github.com/tomasbjerre/git-changelog-lib/commit/cb23e700107d5c1) Tomas Bjerre *2016-03-20 06:52:21*

**Updating test cases after changing test-branch**


[ca6ee75dc629894](https://github.com/tomasbjerre/git-changelog-lib/commit/ca6ee75dc629894) Tomas Bjerre *2016-03-19 20:56:45*


## 1.36
### No issue

**Fixing infinite loop in GitRepo**

* Found in 1.19 
* at se.bjurr.gitchangelog.internal.git.GitRepo.toString(GitRepo.java:208) 
* at se.bjurr.gitchangelog.internal.git.GitRepo.getRef(GitRepo.java:174) 
* at se.bjurr.gitchangelog.internal.git.GitRepo.firstCommit(GitRepo.java:195) 
* at se.bjurr.gitchangelog.internal.git.GitRepo.toString(GitRepo.java:208) 
* at se.bjurr.gitchangelog.internal.git.GitRepo.getRef(GitRepo.java:174) 

[a4a15094f367c31](https://github.com/tomasbjerre/git-changelog-lib/commit/a4a15094f367c31) Tomas Bjerre *2016-03-15 20:27:41*


## 1.35
### No issue

**Using okhttp 2.7.5 was using 2.3.0**

* Which caused ClassNotFoundException for okio/ForwardingTimeout. 

[a4bb6103886b293](https://github.com/tomasbjerre/git-changelog-lib/commit/a4bb6103886b293) Tomas Bjerre *2016-03-15 20:12:47*


## 1.34
### GitHub #18   

**Logging error if error invoking GitHub API**


[a040e51847b4f23](https://github.com/tomasbjerre/git-changelog-lib/commit/a040e51847b4f23) Tomas Bjerre *2016-03-15 19:05:58*


### No issue

**Updating CHANGELOG.md**


[2cf820ce81c7b15](https://github.com/tomasbjerre/git-changelog-lib/commit/2cf820ce81c7b15) Tomas Bjerre *2016-03-15 17:52:18*


## 1.33
### GitHub #10   

**Migrate GitHub REST-API to RetroFit library**

* Added auth-token support for GitHub, fixes 
* Added pagination support for GitHub, fixes 

[d29029a38fad6a4](https://github.com/tomasbjerre/git-changelog-lib/commit/d29029a38fad6a4) Jonas Kalderstam *2016-03-15 00:12:45*


### GitHub #15   

**Migrate GitHub REST-API to RetroFit library**

* Added auth-token support for GitHub, fixes 
* Added pagination support for GitHub, fixes 

[d29029a38fad6a4](https://github.com/tomasbjerre/git-changelog-lib/commit/d29029a38fad6a4) Jonas Kalderstam *2016-03-15 00:12:45*


### GitHub #18   

**Introducing custom exceptions**

* Also fixing some issues from PR . Removing duplicate Gson, System.out, throwing exceptions. 

[4cb3f757f8a6b9f](https://github.com/tomasbjerre/git-changelog-lib/commit/4cb3f757f8a6b9f) Tomas Bjerre *2016-03-15 17:22:33*


### No issue

**Update README.md**


[e37195ae3a5bb32](https://github.com/tomasbjerre/git-changelog-lib/commit/e37195ae3a5bb32) Tomas Bjerre *2016-02-22 16:28:48*


## 1.32
### GitHub #16   

**Supplying commit in each issue mentioned in message**


[102431686668a6c](https://github.com/tomasbjerre/git-changelog-lib/commit/102431686668a6c) Tomas Bjerre *2016-02-19 22:18:39*


### No issue

**Avoiding unnecessary Optional in JiraClient**


[feeb61cc1f7ebd3](https://github.com/tomasbjerre/git-changelog-lib/commit/feeb61cc1f7ebd3) Tomas Bjerre *2016-02-19 19:20:56*


## 1.31
### No issue

**Enabling custom Jira client**


[b078d04fbfb978f](https://github.com/tomasbjerre/git-changelog-lib/commit/b078d04fbfb978f) Tomas Bjerre *2016-02-19 18:55:55*


## 1.30
### No issue

**Bugfix, handling multiple tags on same commit**

* Using the last one found. 
* Also refactoring tests. Added a special test-branch to use real GIT instead of fake repo. 

[3c794133dcc1d00](https://github.com/tomasbjerre/git-changelog-lib/commit/3c794133dcc1d00) Tomas Bjerre *2016-02-15 17:41:45*


## 1.29
### No issue

**Bugfix, crashed if all commits in tag were ignored**


[be4143904d6382c](https://github.com/tomasbjerre/git-changelog-lib/commit/be4143904d6382c) Tomas Bjerre *2016-02-14 17:33:46*


## 1.28
### No issue

**Optimizations, reducing memory usage**


[f6eee5c82a46a54](https://github.com/tomasbjerre/git-changelog-lib/commit/f6eee5c82a46a54) Tomas Bjerre *2016-02-14 16:33:19*

**Updating CHANGELOG.md**


[58fcebc50354375](https://github.com/tomasbjerre/git-changelog-lib/commit/58fcebc50354375) Tomas Bjerre *2016-02-13 17:14:37*


## 1.27
### No issue

**Bugfix, was not including first tag**


[ca96998dbd2f376](https://github.com/tomasbjerre/git-changelog-lib/commit/ca96998dbd2f376) Tomas Bjerre *2016-02-13 08:52:27*


## 1.26
### GitHub #13   

**Rewriting GitRepo to make it faster**


[60811d245d20669](https://github.com/tomasbjerre/git-changelog-lib/commit/60811d245d20669) Tomas Bjerre *2016-02-13 08:24:24*

**Identified performance issue as GitRepo:getTags()**

* Updating performance test to reveal it. 

[be9fb93023f13ad](https://github.com/tomasbjerre/git-changelog-lib/commit/be9fb93023f13ad) Tomas Bjerre *2016-02-11 17:48:06*


## 1.25
### GitHub #13   

**Letting JGit determine new commits between refs**

* Also changing changelog template. 
* Also trimming messageTitle variable. 

[5b307bd00b47e83](https://github.com/tomasbjerre/git-changelog-lib/commit/5b307bd00b47e83) Tomas Bjerre *2016-02-10 17:31:13*


## 1.24
### No issue

**Added variables: messageTitle, messageBody, messageItems**


[80609aa22d11adf](https://github.com/tomasbjerre/git-changelog-lib/commit/80609aa22d11adf) Tomas Bjerre *2016-02-09 19:12:15*

**Maven Central version badge in README.md**


[4e25e7e2cf5d5ec](https://github.com/tomasbjerre/git-changelog-lib/commit/4e25e7e2cf5d5ec) Tomas Bjerre *2016-01-31 21:12:35*

**Better error message when commit not found**


[1e5cb6c76a681aa](https://github.com/tomasbjerre/git-changelog-lib/commit/1e5cb6c76a681aa) Tomas Bjerre *2016-01-31 12:15:00*


## 1.23
### No issue

**Sorting tags by committime, not committime formatted string**


[a9fc5a30a82c320](https://github.com/tomasbjerre/git-changelog-lib/commit/a9fc5a30a82c320) Tomas Bjerre *2016-01-31 10:30:24*

**Implementing toString on API mode objects**


[283c20a3ae4b7b6](https://github.com/tomasbjerre/git-changelog-lib/commit/283c20a3ae4b7b6) Tomas Bjerre *2016-01-31 10:10:30*

**Lgging exception in rest client**


[e529afb2a911123](https://github.com/tomasbjerre/git-changelog-lib/commit/e529afb2a911123) Tomas Bjerre *2016-01-31 08:04:33*


## 1.22
### No issue

**Removing accidently added duplicate Gson dependency**


[7fde8935d59456d](https://github.com/tomasbjerre/git-changelog-lib/commit/7fde8935d59456d) Tomas Bjerre *2016-01-30 17:07:40*


## 1.21
### No issue

**Correcting revision logging**

* Resetting Jira and GitHub clients before tests. Was having troubles with the cache not being invalidated between tests. 

[00b37d11101f8d1](https://github.com/tomasbjerre/git-changelog-lib/commit/00b37d11101f8d1) Tomas Bjerre *2016-01-30 16:39:21*


## 1.20
### No issue

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
### GitHub #11   

**Removing command line code**


[a7b20fdea564ff2](https://github.com/tomasbjerre/git-changelog-lib/commit/a7b20fdea564ff2) Tomas Bjerre *2016-01-27 18:14:33*


### No issue

**Changing Jenkins plugin link to point at JenkinsCI**


[d405f4e8ae5f34f](https://github.com/tomasbjerre/git-changelog-lib/commit/d405f4e8ae5f34f) Tomas Bjerre *2015-12-11 07:56:23*

**Adding example template to readme**


[296d811a7ec3492](https://github.com/tomasbjerre/git-changelog-lib/commit/296d811a7ec3492) Tomas Bjerre *2015-12-10 17:12:44*


## 1.18
### No issue

**Downgrading JGIT to 3.6.2 to be compatible with its older API**


[25fad813507296c](https://github.com/tomasbjerre/git-changelog-lib/commit/25fad813507296c) Tomas Bjerre *2015-12-08 18:44:25*


## 1.17
### No issue

**Allowing variables to be extended with custom context:s**


[c7e3a39354cb339](https://github.com/tomasbjerre/git-changelog-lib/commit/c7e3a39354cb339) Tomas Bjerre *2015-12-05 14:30:21*


## 1.16
### No issue

**Peeling references**

* To support annotated tags 

[b818fd6a2f1207c](https://github.com/tomasbjerre/git-changelog-lib/commit/b818fd6a2f1207c) Tomas Bjerre *2015-12-05 14:01:56*


## 1.15
### No issue

**Using JGit to find repo folder correctly**

* And JsonPath 2.1.0 

[2d0c1f1102387d0](https://github.com/tomasbjerre/git-changelog-lib/commit/2d0c1f1102387d0) Tomas Bjerre *2015-12-04 22:09:16*


## 1.14
### No issue

**Caching requests to GitHub and Jira**


[2b9b6112244d1aa](https://github.com/tomasbjerre/git-changelog-lib/commit/2b9b6112244d1aa) Tomas Bjerre *2015-12-01 21:34:52*


## 1.13
### No issue

**Avoiding crash if GitHub issue cant be found**


[abe713cb069aa1f](https://github.com/tomasbjerre/git-changelog-lib/commit/abe713cb069aa1f) Tomas Bjerre *2015-11-23 21:44:23*


## 1.12
### GitHub #2   

**Integrating with GitHub**


[45af766856ac703](https://github.com/tomasbjerre/git-changelog-lib/commit/45af766856ac703) Tomas Bjerre *2015-11-22 19:51:40*


### GitHub #3   

**Integrating with Jira**


[30a1095331ded15](https://github.com/tomasbjerre/git-changelog-lib/commit/30a1095331ded15) Tomas Bjerre *2015-11-23 17:32:23*


## 1.11
### No issue

**Changing master reference constan, to just master**

* refs/heads/master may not exist, perhaps its refs/remotes/origin/master 

[c655cbd914d06f3](https://github.com/tomasbjerre/git-changelog-lib/commit/c655cbd914d06f3) Tomas Bjerre *2015-11-21 15:36:03*

**doc**


[8c95fd4870872d4](https://github.com/tomasbjerre/git-changelog-lib/commit/8c95fd4870872d4) Tomas Bjerre *2015-11-21 14:24:35*


## 1.10
### No issue

**Adding test cases for MediaWiki integration**

* Linking readme to screenshots 
* Not encoding html-tags for MediaWiki, to enable setting the toclimit with a tag. 

[c8da374a92183d2](https://github.com/tomasbjerre/git-changelog-lib/commit/c8da374a92183d2) Tomas Bjerre *2015-11-21 14:18:19*

**Adding example html-template**


[7749ddfa42b4034](https://github.com/tomasbjerre/git-changelog-lib/commit/7749ddfa42b4034) Tomas Bjerre *2015-11-20 21:38:13*


## 1.9
### No issue

**Adding custom issues correctly #bugfix**


[b5f03710018a65d](https://github.com/tomasbjerre/git-changelog-lib/commit/b5f03710018a65d) Tomas Bjerre *2015-11-20 21:12:55*

**Better regular expression for extracting readable part of tag name #feature**


[d669ae9e7d00e52](https://github.com/tomasbjerre/git-changelog-lib/commit/d669ae9e7d00e52) Tomas Bjerre *2015-11-20 20:34:55*


## 1.8
### No issue

**Setting default setting of ignore commits regexp #bugfix**


[c226865b64ce01c](https://github.com/tomasbjerre/git-changelog-lib/commit/c226865b64ce01c) Tomas Bjerre *2015-11-20 20:24:08*


## 1.7
### No issue

**Using correct reference #bugfix**


[388a3a851f665c1](https://github.com/tomasbjerre/git-changelog-lib/commit/388a3a851f665c1) Tomas Bjerre *2015-11-20 19:08:45*


## 1.6
### No issue

**Some more work to get the lib working with Jenkins plugin**


[bfa6753630efcdb](https://github.com/tomasbjerre/git-changelog-lib/commit/bfa6753630efcdb) Tomas Bjerre *2015-11-20 17:03:04*

**Better error if not setting fromRepo**


[f978c6fd9c72233](https://github.com/tomasbjerre/git-changelog-lib/commit/f978c6fd9c72233) Tomas Bjerre *2015-11-19 21:21:04*


## 1.5
### No issue

**Not loading default settings by default when using API**


[d3e8656074af987](https://github.com/tomasbjerre/git-changelog-lib/commit/d3e8656074af987) Tomas Bjerre *2015-11-19 20:56:47*

**updating changelog**


[64f318ca8f10ba8](https://github.com/tomasbjerre/git-changelog-lib/commit/64f318ca8f10ba8) Tomas Bjerre *2015-11-19 19:46:44*


## 1.4
### GitHub #7   

**Adding hasIssue hasLink to readme doc**


[fbb455921921ddc](https://github.com/tomasbjerre/git-changelog-lib/commit/fbb455921921ddc) Tomas Bjerre *2015-11-18 19:41:03*


### No issue

**Making sure several custom issue patterns can be set with API**


[49c51f0fa73f35c](https://github.com/tomasbjerre/git-changelog-lib/commit/49c51f0fa73f35c) Tomas Bjerre *2015-11-19 19:40:27*

**Updating mediawiki screenshot**


[1669b02acf8c9fb](https://github.com/tomasbjerre/git-changelog-lib/commit/1669b02acf8c9fb) Tomas Bjerre *2015-11-18 21:00:59*


## 1.3
### GitHub #7   

**Adding booleans to check if link and/or issue exists in issue**


[210e963a07b8a7b](https://github.com/tomasbjerre/git-changelog-lib/commit/210e963a07b8a7b) Tomas Bjerre *2015-11-18 19:06:00*


### No issue

**Adding new mediawiki screenshot**


[467d126c87b734e](https://github.com/tomasbjerre/git-changelog-lib/commit/467d126c87b734e) Tomas Bjerre *2015-11-18 19:17:48*

**Update README.md**


[49afd799253a777](https://github.com/tomasbjerre/git-changelog-lib/commit/49afd799253a777) Tomas Bjerre *2015-11-18 06:55:50*

**doc**


[a083453437d20bd](https://github.com/tomasbjerre/git-changelog-lib/commit/a083453437d20bd) Tomas Bjerre *2015-11-17 20:29:42*


## 1.2
### GitHub #4   

**MediaWiki integration**


[f45829b2c6a78d7](https://github.com/tomasbjerre/git-changelog-lib/commit/f45829b2c6a78d7) Tomas Bjerre *2015-11-17 19:27:03*


### No issue

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
### No issue

**Car remove issue from commit message in changelog #feature**


[ec026ad2f25c612](https://github.com/tomasbjerre/git-changelog-lib/commit/ec026ad2f25c612) Tomas Bjerre *2015-11-15 11:37:18*

**Improving test output #feature**


[3191a431aff2b20](https://github.com/tomasbjerre/git-changelog-lib/commit/3191a431aff2b20) Tomas Bjerre *2015-11-15 10:58:51*

**Adding generated CHANGELOG.md #feature**

* Correcting faulty precondition check, file output argument could not be set. #bug 

[b3ddd5fdad30f41](https://github.com/tomasbjerre/git-changelog-lib/commit/b3ddd5fdad30f41) Tomas Bjerre *2015-11-15 09:48:35*

**Customizing changelog #feature**


[59830c6f2a394fe](https://github.com/tomasbjerre/git-changelog-lib/commit/59830c6f2a394fe) Tomas Bjerre *2015-11-15 09:24:58*

**Adding script to generate changelog**

* Finding git repo in parent folders correctly #bugfix 

[82be7c398d445bd](https://github.com/tomasbjerre/git-changelog-lib/commit/82be7c398d445bd) Tomas Bjerre *2015-11-15 09:15:38*

**More settings can be set from command line**

* More testing 

[ba9d565ddd15d1b](https://github.com/tomasbjerre/git-changelog-lib/commit/ba9d565ddd15d1b) Tomas Bjerre *2015-11-15 08:58:00*


## 1.0
### No issue

**Doc**


[83a8a7b2f96ba88](https://github.com/tomasbjerre/git-changelog-lib/commit/83a8a7b2f96ba88) Tomas Bjerre *2015-11-14 19:36:21*

**Test**


[5c54861708099d9](https://github.com/tomasbjerre/git-changelog-lib/commit/5c54861708099d9) Tomas Bjerre *2015-11-14 19:00:17*

**Work for 1.0**


[051effe72405e78](https://github.com/tomasbjerre/git-changelog-lib/commit/051effe72405e78) Tomas Bjerre *2015-11-14 12:14:41*

**Initial commit**


[5aaeb907f68915a](https://github.com/tomasbjerre/git-changelog-lib/commit/5aaeb907f68915a) Tomas Bjerre *2015-11-14 09:46:23*


