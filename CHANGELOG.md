# git-changelog-lib changelog

Changelog of git-changelog-lib.

## [1.152.0](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.152.0) (2021-06-03)



### Features

-  subString and ifMatches helpers ([c64a1](https://github.com/tomasbjerre/git-changelog-lib/commit/c64a1bc9312b6ec))  





## [1.151.0](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.151.0) (2021-06-03)



### Features

-  ifEquals helper ([94344](https://github.com/tomasbjerre/git-changelog-lib/commit/9434438ec8ea325))  





## [1.150.0](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.150.0) (2021-06-02)



### Features

-  regexp in `commitType` and `commitScope` ([2d3c6](https://github.com/tomasbjerre/git-changelog-lib/commit/2d3c6660bfe8d64))  





## [1.149.6](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.149.6) (2021-06-01)







### Other changes

**using Gradle 7 and java-library plugin**


[e8a4c](https://github.com/tomasbjerre/git-changelog-lib/commit/e8a4cef6abfcc60) Tomas Bjerre *2021-06-01 15:39:56*

## [1.149.4](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.149.4) (2021-05-31)



### Features

-  removing dependencies on Guava ([6402c](https://github.com/tomasbjerre/git-changelog-lib/commit/6402c759db491ca))  





## [1.149.2](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.149.2) (2021-05-27)





### Bug Fixes

-  multiline footer/paragraphs ([0fb28](https://github.com/tomasbjerre/git-changelog-lib/commit/0fb2877848bb21c))  



## [1.149.0](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.149.0) (2021-05-26)



### Features

-  more conventional helpers ([3f17b](https://github.com/tomasbjerre/git-changelog-lib/commit/3f17b1ecb3bfb6a))  





## [1.148.1](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.148.1) (2021-05-25)








## [1.148.0](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.148.0) (2021-05-25)



### Features

-  **helpers**  and ([3572c](https://github.com/tomasbjerre/git-changelog-lib/commit/3572c9b070a8972))  





## [1.147.5](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.147.5) (2021-05-25)





### Bug Fixes

-  naming ([759e0](https://github.com/tomasbjerre/git-changelog-lib/commit/759e07f97345011))  



## [1.147.4](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.147.4) (2021-05-25)





### Bug Fixes

-  **helpers**  they did not work ([dba7b](https://github.com/tomasbjerre/git-changelog-lib/commit/dba7b31c364a63b))  



## [1.147.3](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.147.3) (2021-05-25)



### Features

-  adding some helpers to better support conventional commits ([8bed7](https://github.com/tomasbjerre/git-changelog-lib/commit/8bed7566d19c045))  [#92](https://github.com/tomasbjerre/git-changelog-lib/issues/92)  




### Other changes

**more helpers**


[ecad3](https://github.com/tomasbjerre/git-changelog-lib/commit/ecad3315e6f3209) Tomas Bjerre *2021-05-25 14:06:44*
**Remove reverted commits from commits lists**


[a4631](https://github.com/tomasbjerre/git-changelog-lib/commit/a4631b0bf4f4ab8) Tomas Bjerre *2021-05-25 13:28:17*
**Java 8 syntax**


[47599](https://github.com/tomasbjerre/git-changelog-lib/commit/47599a8ca95d380) Tomas Bjerre *2021-05-25 03:47:23*

## [1.147.2](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.147.2) (2021-05-24)





### Bug Fixes

-  dont require semantic patterns to get highest tag ([38fc5](https://github.com/tomasbjerre/git-changelog-lib/commit/38fc504697f8d04))  



## [1.147.1](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.147.1) (2021-05-23)





### Bug Fixes

-  dont use integrations when determining versions ([601c0](https://github.com/tomasbjerre/git-changelog-lib/commit/601c009bff8d551))  



## [1.146.0](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.146.0) (2021-05-23)



### Features

-  renaming methods in api ([95a7b](https://github.com/tomasbjerre/git-changelog-lib/commit/95a7b6623a92c36))  





## [1.145.0](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.145.0) (2021-05-23)



### Features

-  support semantic versioning ([53209](https://github.com/tomasbjerre/git-changelog-lib/commit/53209c9e5da9c75))  [#92](https://github.com/tomasbjerre/git-changelog-lib/issues/92)  





## [1.144.4](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.144.4) (2021-04-02)







### Other changes

**using new build script**


[82726](https://github.com/tomasbjerre/git-changelog-lib/commit/82726fdb6608dca) Tomas Bjerre *2021-04-02 18:37:49*

## [1.96](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.96) (2021-03-29)







### Other changes

**pretty printing output JENKINS-65252**


[bea18](https://github.com/tomasbjerre/git-changelog-lib/commit/bea18ab90db148c) Tomas Bjerre *2021-03-29 15:33:00*

## [1.95](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.95) (2021-01-18)







### Other changes

**Removing default ignore filter on message**

* It was: &#x60;^\[maven-release-plugin\].*|^\[Gradle Release Plugin\].*|^Merge.*&quot;&#x60; 
* But users are confused by this and it is probably better to have no filter by default. 

[a57e3](https://github.com/tomasbjerre/git-changelog-lib/commit/a57e321f08cea10) Tomas Bjerre *2021-01-18 16:46:13*
**Switching to Handlebars from Mustache**

* Removing Mediawiki support. Because I have not been able to make it work. https://stackoverflow.com/questions/45779754/cannot-authenticate-with-mediawiki-1-28-api 
* Steeping up JGit to latest release. 3.6.2.201501210735-r to 5.10.0.202012080955-r. 
* Refactoring tests to use Approvals. 

[7eba3](https://github.com/tomasbjerre/git-changelog-lib/commit/7eba3d038b884b8) Tomas Bjerre *2020-12-24 07:11:41*

## [1.94](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.94) (2020-11-18)







### Other changes

**Adjustments after merge of PR #94**


[6e07d](https://github.com/tomasbjerre/git-changelog-lib/commit/6e07d0b76da227e) Tomas Bjerre *2020-11-18 17:14:10*
**Add an option to filter the commits using a path filter string**

* This works exactly like &#x60;git log -- path&#x60; 

[94df8](https://github.com/tomasbjerre/git-changelog-lib/commit/94df887471f9f97) Gabriel Einsdorf *2020-11-18 13:48:10*

## [1.93](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.93) (2020-11-07)







### Other changes

**Support custom headers to JIRA to bypass 2 factor auth**


[c4307](https://github.com/tomasbjerre/git-changelog-lib/commit/c43070c5cd20162) Yauheni Biruk *2020-11-07 13:53:10*

## [1.92](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.92) (2020-02-20)







### Other changes

**naming in api tomasbjerre/git-changelog-lib#78**


[21ef6](https://github.com/tomasbjerre/git-changelog-lib/commit/21ef6027c82814e) Tomas Bjerre *2020-02-20 16:31:35*
**add support for jira tokens**


[fc754](https://github.com/tomasbjerre/git-changelog-lib/commit/fc75402ade75684) Dmitry Mamchur *2020-02-20 15:43:30*
**Spotbugs**


[3149f](https://github.com/tomasbjerre/git-changelog-lib/commit/3149f34c0b2a8e5) Tomas Bjerre *2019-10-08 19:39:27*
**Create FUNDING.yml**


[f00b0](https://github.com/tomasbjerre/git-changelog-lib/commit/f00b0e46124db77) Tomas Bjerre *2019-09-28 07:06:44*

## [1.91](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.91) (2019-06-11)







### Other changes

**Removing dependency on javax.xml**


[65152](https://github.com/tomasbjerre/git-changelog-lib/commit/6515299f938c982) Tomas Bjerre *2019-06-11 16:54:48*

## [1.90](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.90) (2019-06-11)







### Other changes

**JDK 11 compatible**


[cd916](https://github.com/tomasbjerre/git-changelog-lib/commit/cd9162d9035e450) Tomas Bjerre *2019-06-11 16:21:57*
**OpenJDK 11 in Travis**


[ba751](https://github.com/tomasbjerre/git-changelog-lib/commit/ba751250bef78f1) Tomas Bjerre *2019-06-11 16:15:19*
**Clearer exception stacktrace #73**


[0a8d3](https://github.com/tomasbjerre/git-changelog-lib/commit/0a8d3dc6aa79b1d) Tomas Bjerre *2019-05-17 05:03:23*

## [1.89](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.89) (2019-02-16)







### Other changes

**Avoiding global state in JiraClientFactory**


[1aa1c](https://github.com/tomasbjerre/git-changelog-lib/commit/1aa1c4ca13d06a9) Tomas Bjerre *2019-02-16 17:08:53*

## [1.88](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.88) (2019-01-08)







### Other changes

**Setting version to fix faulty release**


[a562b](https://github.com/tomasbjerre/git-changelog-lib/commit/a562becc5f40a2f) Tomas Bjerre *2019-01-08 17:02:29*
**Setting version to fix faulty release**


[10545](https://github.com/tomasbjerre/git-changelog-lib/commit/10545b0bd17a01f) Tomas Bjerre *2019-01-08 16:46:06*
**update README**


[7a056](https://github.com/tomasbjerre/git-changelog-lib/commit/7a0565cf32c0504) Lukas Läderach *2019-01-07 21:16:05*
**add unit tests for jira linked issues**


[c7e0a](https://github.com/tomasbjerre/git-changelog-lib/commit/c7e0aa691c5f26b) Lukas Läderach *2019-01-07 21:03:00*
**Add support for LinkedIssues (inward, outward) in Jira (caused by, references)**


[794eb](https://github.com/tomasbjerre/git-changelog-lib/commit/794eb3af4173fdc) Lukas Läderach *2018-12-13 09:41:39*

## [1.85](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.85) (2018-10-27)







### Other changes

**Correcting GitLab integration after #70**


[7d1e6](https://github.com/tomasbjerre/git-changelog-lib/commit/7d1e69bf235452e) Tomas Bjerre *2018-10-27 17:48:50*

## [1.84](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.84) (2018-10-27)







### Other changes

**upgrade gitlab dependency**

* Gitlab v3 has been deprecated (see https://about.gitlab.com/2018/06/01/api-v3-removal-impending/) and a new version was released. 

[f125a](https://github.com/tomasbjerre/git-changelog-lib/commit/f125a10ae48f178) Jorge *2018-10-27 16:14:52*
**Moving some test cases**


[05540](https://github.com/tomasbjerre/git-changelog-lib/commit/055401cd6153ef1) Tomas Bjerre *2018-10-27 14:56:58*
**Testing issue types #68**


[d8121](https://github.com/tomasbjerre/git-changelog-lib/commit/d812191c55684be) Tomas Bjerre *2018-10-27 10:32:47*

## [1.83](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.83) (2018-10-27)







### Other changes

**Adding booleans isJira, isGitHub... #68**


[44eff](https://github.com/tomasbjerre/git-changelog-lib/commit/44eff0421308787) Tomas Bjerre *2018-10-27 05:36:13*
**Doc - correct typo in authorEmail**


[f06cc](https://github.com/tomasbjerre/git-changelog-lib/commit/f06cc266d41e4d6) Beth Skurrie *2018-10-09 01:21:11*

## [1.82](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.82) (2018-09-13)







### Other changes

**Avoiding NPE:s and some refactoring**


[07a0e](https://github.com/tomasbjerre/git-changelog-lib/commit/07a0e34440540b6) Tomas Bjerre *2018-09-13 06:54:32*

## [1.81](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.81) (2018-09-12)







### Other changes

**Correcting ownerName when dot in repo name**


[56f23](https://github.com/tomasbjerre/git-changelog-lib/commit/56f23fe9eff5487) Tomas Bjerre *2018-09-12 14:41:21*
**Trimming from/to refs**


[b594b](https://github.com/tomasbjerre/git-changelog-lib/commit/b594b083087c90c) Tomas Bjerre *2018-07-16 11:54:58*

## [1.80](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.80) (2018-05-05)







### Other changes

**Closing connection to Jira**


[d1bbb](https://github.com/tomasbjerre/git-changelog-lib/commit/d1bbba0172779da) Tomas Bjerre *2018-05-05 07:42:30*
**Doc**


[fd599](https://github.com/tomasbjerre/git-changelog-lib/commit/fd5990de3e69905) Tomas Bjerre *2018-04-20 15:15:48*

## [1.79](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.79) (2018-01-09)







### Other changes

**Bumping version to fix faulty release**


[df186](https://github.com/tomasbjerre/git-changelog-lib/commit/df186edf843a598) Tomas Bjerre *2018-01-09 20:53:41*
**Removing state from GitHub Client**

* The client was created once, with one API, and kept for all future invcations. So that if a changelog was created for one repo (A) and then for a repo (B), then B would use the API from A. Resulting in wrong issue information in B. 

[8ef76](https://github.com/tomasbjerre/git-changelog-lib/commit/8ef76f5c6653ab3) Tomas Bjerre *2018-01-09 20:46:33*
**changelog.json: Fix invalid JSON**


[46d37](https://github.com/tomasbjerre/git-changelog-lib/commit/46d37d70e2f9ffe) Chad Horohoe *2018-01-03 02:31:13*

## [1.77](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.77) (2017-12-30)







### Other changes

**Closing RevWalk JENKINS-19994**


[9f7c7](https://github.com/tomasbjerre/git-changelog-lib/commit/9f7c782dc9be919) Tomas Bjerre *2017-12-30 20:07:57*
**Using UTF-8, instead of default**


[7fe5f](https://github.com/tomasbjerre/git-changelog-lib/commit/7fe5f6b9860efe7) Tomas Bjerre *2017-12-24 21:59:34*

## [1.76](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.76) (2017-12-19)







### Other changes

**Updating Doc after merge of #59**


[cef15](https://github.com/tomasbjerre/git-changelog-lib/commit/cef15b8443eacef) Tomas Bjerre *2017-12-19 10:43:45*
**README updated**


[77cec](https://github.com/tomasbjerre/git-changelog-lib/commit/77ceccd935bc8cd) Michael Hauck *2017-12-18 14:18:26*
**Fixing Model and Parser**


[761f4](https://github.com/tomasbjerre/git-changelog-lib/commit/761f40fd6237226) Michael Hauck *2017-12-18 14:01:38*
**Adding support for Jira Issue Description**


[6472a](https://github.com/tomasbjerre/git-changelog-lib/commit/6472aa221cc6e2d) Michael Hauck *2017-12-18 12:21:11*
**Doc**


[42e73](https://github.com/tomasbjerre/git-changelog-lib/commit/42e73bdf05c384a) Tomas Bjerre *2017-12-03 19:47:23*

## [1.75](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.75) (2017-12-03)







### Other changes

**Using shared build scripts**


[6c906](https://github.com/tomasbjerre/git-changelog-lib/commit/6c906db94a069a7) Tomas Bjerre *2017-12-03 07:45:37*
**Doc**


[74fd3](https://github.com/tomasbjerre/git-changelog-lib/commit/74fd3ae5e58aa76) Tomas Bjerre *2017-11-18 12:14:20*

## [1.74](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.74) (2017-11-18)







### Other changes

**Updating build tools and removing shaddow jar**


[57dc0](https://github.com/tomasbjerre/git-changelog-lib/commit/57dc01ac9223fd7) Tomas Bjerre *2017-11-18 12:00:17*
**Avoid fetching from integrations if not used #58**

* Not fetching information from integrations (GitHub, GitLab, Jira) if that information is not used in the template. 

[e87ef](https://github.com/tomasbjerre/git-changelog-lib/commit/e87efa3246489c2) Tomas Bjerre *2017-11-18 11:26:24*

## [1.73](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.73) (2017-11-02)







### Other changes

**Avoiding usage of Guava Objects**

* Because it results in NoSuchMethodException when newer Guava version exists on classpath. Where Objects is replaced with MoreObjects. 

[94dc5](https://github.com/tomasbjerre/git-changelog-lib/commit/94dc5eee25f9414) Tomas Bjerre *2017-11-02 17:06:49*

## [1.72](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.72) (2017-09-03)







### Other changes

**Disabling MediaWiki integration tests**


[b82e2](https://github.com/tomasbjerre/git-changelog-lib/commit/b82e25da5b3d681) Tomas Bjerre *2017-09-03 09:15:28*
**Travis with JDK8**


[db79d](https://github.com/tomasbjerre/git-changelog-lib/commit/db79d0fb4f7a33d) Tomas Bjerre *2017-09-01 19:25:13*
**Rewrite MediaWiki Client for Botuser #51**


[9cd8d](https://github.com/tomasbjerre/git-changelog-lib/commit/9cd8d1dfa42aea1) Tomas Bjerre *2017-08-31 17:23:58*

## [1.71](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.71) (2017-07-25)







### Other changes

**Correcting owner/repo name cloneUrl without dot git**


[eac21](https://github.com/tomasbjerre/git-changelog-lib/commit/eac21b71186099b) Tomas Bjerre *2017-07-25 18:21:50*

## [1.70](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.70) (2017-07-24)







### Other changes

**Gathering repo provider information #49**

* Getting ownerName and repoName from clone URL. 
* Setting GitLab server and GitHub API from clone URL. 

[dd849](https://github.com/tomasbjerre/git-changelog-lib/commit/dd8497034624b12) Tomas Bjerre *2017-07-23 19:10:43*
**Cleaning**


[3c0bf](https://github.com/tomasbjerre/git-changelog-lib/commit/3c0bf2f7885bff3) Tomas Bjerre *2017-07-16 05:55:55*

## [1.69](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.69) (2017-07-08)







### Other changes

**Adjustments after merge of #47**

* Using the Date data type instead of String to supply ignoreCommitsOlderThan. 
* Making ignoreCommits more effective. 
* Also cleaning up unrelated parts of the API. Use File instead of String to supply changelog file. Enabling write changelog to Writer. 

[121e6](https://github.com/tomasbjerre/git-changelog-lib/commit/121e65493ec764b) Tomas Bjerre *2017-07-07 20:04:00*
**Fix CommitsWithMesssage typo -> CommitsWithMessage**


[67667](https://github.com/tomasbjerre/git-changelog-lib/commit/67667ba3a5258fd) jakob *2017-07-04 00:16:47*
**Add ignoreCommitsOlderThan date limiting**

* The rationale is that perhaps projects might: 
* use a git-based changelog to inform coworkers, rather than 
* customers, making a more short-lived &quot;news&quot;-style log desirable, 
* not (yet?) use frequent-enough tags in their repository, 
* want to provide a sense for the liveliness of a project 

[67181](https://github.com/tomasbjerre/git-changelog-lib/commit/671816d3dc3d60a) jakob *2017-07-03 23:53:47*
**Fix typo in readableTagName javadoc**


[63100](https://github.com/tomasbjerre/git-changelog-lib/commit/6310062f7a0470c) jakob *2017-05-31 18:45:11*

## [1.68](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.68) (2017-04-14)







### Other changes

**doc**


[92fca](https://github.com/tomasbjerre/git-changelog-lib/commit/92fca3cfbb690ae) Tomas Bjerre *2017-04-14 09:08:25*
**tag time added to tag model**


[8c583](https://github.com/tomasbjerre/git-changelog-lib/commit/8c5837402c4a802) Alik Kurdyukov *2017-04-11 18:33:29*

## [1.67](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.67) (2017-03-25)







### Other changes

**GitLab integration #2**

* Adding it to the API. 

[743ab](https://github.com/tomasbjerre/git-changelog-lib/commit/743ab5566d39083) Tomas Bjerre *2017-03-25 16:31:51*

## [1.66](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.66) (2017-03-25)







### Other changes

**GitLab integration #42**


[eb3d1](https://github.com/tomasbjerre/git-changelog-lib/commit/eb3d1ee8a5ed370) Tomas Bjerre *2017-03-25 15:06:44*

## [1.65](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.65) (2017-03-20)







### Other changes

**doc**


[9b2d4](https://github.com/tomasbjerre/git-changelog-lib/commit/9b2d45154695881) Tomas Bjerre *2017-03-20 18:14:10*
**fix jira labels**


[dbf30](https://github.com/tomasbjerre/git-changelog-lib/commit/dbf309010fbcd76) Heorhi Bisiaryn *2017-03-20 15:23:57*

## [1.64](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.64) (2017-03-18)







### Other changes

**Adding issueType and labels attributes #40**


[6db44](https://github.com/tomasbjerre/git-changelog-lib/commit/6db44d44152f5da) Tomas Bjerre *2017-03-18 09:10:13*
**Google java code standard**


[b4317](https://github.com/tomasbjerre/git-changelog-lib/commit/b4317dd44e3ddcc) Tomas Bjerre *2017-03-17 16:22:01*
**jira issue type feature**


[fdd72](https://github.com/tomasbjerre/git-changelog-lib/commit/fdd7229f9b2f183) Heorhi Bisiaryn *2017-03-15 11:54:01*

## [1.63](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.63) (2017-03-01)







### Other changes

**Adding timeout, 10 seconds**


[1c3ca](https://github.com/tomasbjerre/git-changelog-lib/commit/1c3ca2cf2d1e598) Tomas Bjerre *2017-03-01 18:13:14*
**doc**


[4de5c](https://github.com/tomasbjerre/git-changelog-lib/commit/4de5c284efe8c52) Tomas Bjerre *2017-02-19 07:21:42*
**Set theme jekyll-theme-slate**


[e569b](https://github.com/tomasbjerre/git-changelog-lib/commit/e569b0c8dfe5957) Tomas Bjerre *2017-01-12 03:04:46*
**Adding HTML example supplied by Joel Eriksson**

* Also moving example templates to examples folder. 

[4c554](https://github.com/tomasbjerre/git-changelog-lib/commit/4c554557c16f68d) Tomas Bjerre *2016-12-21 16:29:21*

## [1.62](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.62) (2016-12-17)







### Other changes

**mergeServiceFiles in fat jar**


[8b2fb](https://github.com/tomasbjerre/git-changelog-lib/commit/8b2fb9b3219ba47) Tomas Bjerre *2016-12-17 14:29:25*

## [1.61](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.61) (2016-12-16)







### Other changes

**Relocate packages to avoid classpath issues #38**


[79f56](https://github.com/tomasbjerre/git-changelog-lib/commit/79f56692487b731) Tomas Bjerre *2016-12-16 20:02:28*

## [1.60](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.60) (2016-12-16)







### Other changes

**Relocate packages to avoid classpath issues #38**


[8d2aa](https://github.com/tomasbjerre/git-changelog-lib/commit/8d2aac7c6c174f1) Tomas Bjerre *2016-12-16 16:35:06*

## [1.59](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.59) (2016-12-16)







### Other changes

**Relocate packages to avoid classpath issues #38**


[65372](https://github.com/tomasbjerre/git-changelog-lib/commit/653720603542acb) Tomas Bjerre *2016-12-16 16:26:45*

## [1.58](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.58) (2016-10-22)







### Other changes

**Adding annotation to context of tag #36**


[ecd85](https://github.com/tomasbjerre/git-changelog-lib/commit/ecd852cfa40b0a7) Tomas Bjerre *2016-10-22 07:23:29*

## [1.57](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.57) (2016-10-05)







### Other changes

**Adding merge boolean to commits #35**


[2d918](https://github.com/tomasbjerre/git-changelog-lib/commit/2d9189f421254f2) Tomas Bjerre *2016-10-05 17:04:13*

## [1.56](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.56) (2016-08-11)







### Other changes

**Fixing testcases after merge of #31**


[50dcd](https://github.com/tomasbjerre/git-changelog-lib/commit/50dcd850d6bec5d) Tomas Bjerre *2016-08-11 14:14:57*
**issue key was missing in issue link**


[3abb1](https://github.com/tomasbjerre/git-changelog-lib/commit/3abb1e05e90454d) Ivan Korolev *2016-08-11 11:07:08*

## [1.55](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.55) (2016-08-02)







### Other changes

**Adding {{hashFull}} variable with full commit hash #30**


[7fd99](https://github.com/tomasbjerre/git-changelog-lib/commit/7fd9971d4c2a30b) Tomas Bjerre *2016-08-02 17:00:29*

## [1.54](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.54) (2016-06-27)







### Other changes

**Allowing master branch to be absent #28**


[dd463](https://github.com/tomasbjerre/git-changelog-lib/commit/dd4633ba627695d) Tomas Bjerre *2016-06-27 17:26:30*
**More testing**


[2ae18](https://github.com/tomasbjerre/git-changelog-lib/commit/2ae18a51778cb90) Tomas Bjerre *2016-06-26 17:59:45*

## [1.53](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.53) (2016-06-25)







### Other changes

**Faster**


[9580c](https://github.com/tomasbjerre/git-changelog-lib/commit/9580c7f449e576c) Tomas Bjerre *2016-06-24 23:59:39*

## [1.52](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.52) (2016-06-24)







### Other changes

**Correcting how to find diffing commits ##29**


[2589b](https://github.com/tomasbjerre/git-changelog-lib/commit/2589bd16e3d959b) Tomas Bjerre *2016-06-24 15:46:01*
**Correcting how to find diffing commits #29**


[71760](https://github.com/tomasbjerre/git-changelog-lib/commit/717602bef6a4ab1) Tomas Bjerre *2016-06-24 15:45:29*
**Reverting a723e0a**


[47e35](https://github.com/tomasbjerre/git-changelog-lib/commit/47e35b1dc9dac85) Tomas Bjerre *2016-06-24 15:08:53*

## [1.51](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.51) (2016-06-24)







### Other changes

**Show found refs when not finding ref**


[aef7c](https://github.com/tomasbjerre/git-changelog-lib/commit/aef7ce6729a1b82) Tomas Bjerre *2016-06-24 12:26:00*
**Some more debugging**


[0c597](https://github.com/tomasbjerre/git-changelog-lib/commit/0c59757f08f5eb3) Tomas Bjerre *2016-06-24 09:18:35*

## [1.50](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.50) (2016-06-24)







### Other changes

**Including commits frmo merges #29**


[a723e](https://github.com/tomasbjerre/git-changelog-lib/commit/a723e0a230e3f38) Tomas Bjerre *2016-06-24 08:56:47*

## [1.49](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.49) (2016-06-02)







### Other changes

**Finding first commit in repo as parents of HEAD #28**

* Was looking at parents of master, which may not exist. 

[87fe4](https://github.com/tomasbjerre/git-changelog-lib/commit/87fe4044e587b1b) Tomas Bjerre *2016-06-02 17:55:59*
**Adjusting example html template**


[7ea08](https://github.com/tomasbjerre/git-changelog-lib/commit/7ea087fdbbc9820) Tomas Bjerre *2016-05-20 19:05:36*

## [1.48](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.48) (2016-05-20)







### Other changes

**Testing trailing slash in Jira**


[0c85c](https://github.com/tomasbjerre/git-changelog-lib/commit/0c85c4276ac3dce) Tomas Bjerre *2016-04-28 16:33:44*

## [1.47](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.47) (2016-04-28)







### Other changes

**Removing trailing slash from Jira API URL**

* If it is specified. 

[a94d5](https://github.com/tomasbjerre/git-changelog-lib/commit/a94d576b6fd706e) Tomas Bjerre *2016-04-28 16:18:20*

## [1.46](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.46) (2016-04-24)







### Other changes

**Making API model serializable**


[0e0ce](https://github.com/tomasbjerre/git-changelog-lib/commit/0e0ce5e0c5df526) Tomas Bjerre *2016-04-24 07:29:47*

## [1.45](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.45) (2016-04-13)







### Other changes

**Including correct commits + performance #26 JENKINS-34156**

* Found major performance problem when sorting tags by commit time, fixed. 
* Now not following parents, unless *from* is merged into them. 

[e3106](https://github.com/tomasbjerre/git-changelog-lib/commit/e3106df640b693b) Tomas Bjerre *2016-04-13 18:30:26*

## [1.44](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.44) (2016-04-12)







### Other changes

**JENKINS-34155 Support short SHA**


[27642](https://github.com/tomasbjerre/git-changelog-lib/commit/27642e3db66e67c) Tomas Bjerre *2016-04-12 16:43:21*

## [1.43](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.43) (2016-04-10)







### Other changes

**Parsing commits, oldest first**


[f6a76](https://github.com/tomasbjerre/git-changelog-lib/commit/f6a768567dc03d3) Tomas Bjerre *2016-04-10 09:08:26*

## [1.42](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.42) (2016-04-10)







### Other changes

**Parsing commits, oldest first #23**

* To avoid random behaviour. 

[9817c](https://github.com/tomasbjerre/git-changelog-lib/commit/9817cdf6a1fbe91) Tomas Bjerre *2016-04-10 08:49:45*

## [1.41](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.41) (2016-04-09)







### Other changes

**Traversing commit tree by parents #23**

* To find all commits in all tags. 

[9dcba](https://github.com/tomasbjerre/git-changelog-lib/commit/9dcba5d33fc2e44) Tomas Bjerre *2016-04-09 20:21:58*

## [1.40](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.40) (2016-04-07)







### Other changes

**Adding feature to ignore tags by regexp**

* Also testing tag in feature branch #23 

[8166e](https://github.com/tomasbjerre/git-changelog-lib/commit/8166ec7dfd456df) Tomas Bjerre *2016-04-06 20:39:27*
**Updating CHANGELOG.md**


[96ee3](https://github.com/tomasbjerre/git-changelog-lib/commit/96ee3bb55e4da2d) Tomas Bjerre *2016-03-20 13:25:18*

## [1.39](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.39) (2016-03-20)







### Other changes

**Sorting filtered commits, was random from hash**

* Building with OpenJDK7 in Travis 

[b9761](https://github.com/tomasbjerre/git-changelog-lib/commit/b976129fb928dc1) Tomas Bjerre *2016-03-20 09:42:23*

## [1.38](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.38) (2016-03-20)







### Other changes

**Removing commits without issue, from tags #19**


[22a2c](https://github.com/tomasbjerre/git-changelog-lib/commit/22a2c55c44b6b4d) Tomas Bjerre *2016-03-20 08:38:27*

## [1.37](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.37) (2016-03-20)







### Other changes

**Avoiding parsing commits twice**


[cb23e](https://github.com/tomasbjerre/git-changelog-lib/commit/cb23e700107d5c1) Tomas Bjerre *2016-03-20 06:52:21*
**Ignore commits without issue #19**


[0b70d](https://github.com/tomasbjerre/git-changelog-lib/commit/0b70db9921da8dd) Tomas Bjerre *2016-03-19 22:53:07*
**Avoiding adding commit twice in same issue #21**


[41434](https://github.com/tomasbjerre/git-changelog-lib/commit/414348773e3d658) Tomas Bjerre *2016-03-19 22:16:40*
**Updating test cases after changing test-branch**


[ca6ee](https://github.com/tomasbjerre/git-changelog-lib/commit/ca6ee75dc629894) Tomas Bjerre *2016-03-19 20:56:45*
**Adding issueTypes in context #19**


[a025a](https://github.com/tomasbjerre/git-changelog-lib/commit/a025adfc759313f) Tomas Bjerre *2016-03-19 20:33:11*

## [1.36](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.36) (2016-03-15)







### Other changes

**Fixing infinite loop in GitRepo**

* Found in 1.19 
* at se.bjurr.gitchangelog.internal.git.GitRepo.toString(GitRepo.java:208) 
* at se.bjurr.gitchangelog.internal.git.GitRepo.getRef(GitRepo.java:174) 
* at se.bjurr.gitchangelog.internal.git.GitRepo.firstCommit(GitRepo.java:195) 
* at se.bjurr.gitchangelog.internal.git.GitRepo.toString(GitRepo.java:208) 
* at se.bjurr.gitchangelog.internal.git.GitRepo.getRef(GitRepo.java:174) 

[a4a15](https://github.com/tomasbjerre/git-changelog-lib/commit/a4a15094f367c31) Tomas Bjerre *2016-03-15 20:27:41*

## [1.35](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.35) (2016-03-15)







### Other changes

**Using okhttp 2.7.5 was using 2.3.0**

* Which caused ClassNotFoundException for okio/ForwardingTimeout. 

[a4bb6](https://github.com/tomasbjerre/git-changelog-lib/commit/a4bb6103886b293) Tomas Bjerre *2016-03-15 20:12:47*

## [1.34](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.34) (2016-03-15)







### Other changes

**Logging error if error invoking GitHub API #18**


[a040e](https://github.com/tomasbjerre/git-changelog-lib/commit/a040e51847b4f23) Tomas Bjerre *2016-03-15 19:05:58*
**Updating CHANGELOG.md**


[2cf82](https://github.com/tomasbjerre/git-changelog-lib/commit/2cf820ce81c7b15) Tomas Bjerre *2016-03-15 17:52:18*

## [1.33](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.33) (2016-03-15)







### Other changes

**Introducing custom exceptions**

* Also fixing some issues from PR #18. Removing duplicate Gson, System.out, throwing exceptions. 

[4cb3f](https://github.com/tomasbjerre/git-changelog-lib/commit/4cb3f757f8a6b9f) Tomas Bjerre *2016-03-15 17:22:33*
**Migrate GitHub REST-API to RetroFit library**

* Added auth-token support for GitHub, fixes #10 
* Added pagination support for GitHub, fixes #15 

[d2902](https://github.com/tomasbjerre/git-changelog-lib/commit/d29029a38fad6a4) Jonas Kalderstam *2016-03-15 00:12:45*
**Update README.md**


[e3719](https://github.com/tomasbjerre/git-changelog-lib/commit/e37195ae3a5bb32) Tomas Bjerre *2016-02-22 16:28:48*

## [1.32](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.32) (2016-02-20)







### Other changes

**Supplying commit in each issue mentioned in message #16**


[10243](https://github.com/tomasbjerre/git-changelog-lib/commit/102431686668a6c) Tomas Bjerre *2016-02-19 22:18:39*
**Avoiding unnecessary Optional in JiraClient**


[feeb6](https://github.com/tomasbjerre/git-changelog-lib/commit/feeb61cc1f7ebd3) Tomas Bjerre *2016-02-19 19:20:56*

## [1.31](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.31) (2016-02-19)







### Other changes

**Enabling custom Jira client**


[b078d](https://github.com/tomasbjerre/git-changelog-lib/commit/b078d04fbfb978f) Tomas Bjerre *2016-02-19 18:55:55*

## [1.30](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.30) (2016-02-15)







### Other changes

**Bugfix, handling multiple tags on same commit**

* Using the last one found. 
* Also refactoring tests. Added a special test-branch to use real GIT instead of fake repo. 

[3c794](https://github.com/tomasbjerre/git-changelog-lib/commit/3c794133dcc1d00) Tomas Bjerre *2016-02-15 17:41:45*

## [1.29](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.29) (2016-02-14)







### Other changes

**Bugfix, crashed if all commits in tag were ignored**


[be414](https://github.com/tomasbjerre/git-changelog-lib/commit/be4143904d6382c) Tomas Bjerre *2016-02-14 17:33:46*

## [1.28](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.28) (2016-02-14)







### Other changes

**Optimizations, reducing memory usage**


[f6eee](https://github.com/tomasbjerre/git-changelog-lib/commit/f6eee5c82a46a54) Tomas Bjerre *2016-02-14 16:33:19*
**Updating CHANGELOG.md**


[58fce](https://github.com/tomasbjerre/git-changelog-lib/commit/58fcebc50354375) Tomas Bjerre *2016-02-13 17:14:37*

## [1.27](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.27) (2016-02-13)







### Other changes

**Bugfix, was not including first tag**


[ca969](https://github.com/tomasbjerre/git-changelog-lib/commit/ca96998dbd2f376) Tomas Bjerre *2016-02-13 08:52:27*

## [1.26](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.26) (2016-02-13)







### Other changes

**Rewriting GitRepo to make it faster #13**


[60811](https://github.com/tomasbjerre/git-changelog-lib/commit/60811d245d20669) Tomas Bjerre *2016-02-13 08:24:24*
**Identified performance issue as GitRepo:getTags() #13**

* Updating performance test to reveal it. 

[be9fb](https://github.com/tomasbjerre/git-changelog-lib/commit/be9fb93023f13ad) Tomas Bjerre *2016-02-11 17:48:06*

## [1.25](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.25) (2016-02-10)







### Other changes

**Letting JGit determine new commits between refs #13**

* Also changing changelog template. 
* Also trimming messageTitle variable. 

[5b307](https://github.com/tomasbjerre/git-changelog-lib/commit/5b307bd00b47e83) Tomas Bjerre *2016-02-10 17:31:13*

## [1.24](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.24) (2016-02-09)







### Other changes

**Added variables: messageTitle, messageBody, messageItems**


[80609](https://github.com/tomasbjerre/git-changelog-lib/commit/80609aa22d11adf) Tomas Bjerre *2016-02-09 19:12:15*
**Maven Central version badge in README.md**


[4e25e](https://github.com/tomasbjerre/git-changelog-lib/commit/4e25e7e2cf5d5ec) Tomas Bjerre *2016-01-31 21:12:35*
**Better error message when commit not found**


[1e5cb](https://github.com/tomasbjerre/git-changelog-lib/commit/1e5cb6c76a681aa) Tomas Bjerre *2016-01-31 12:15:00*

## [1.23](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.23) (2016-01-31)







### Other changes

**Sorting tags by committime, not committime formatted string**


[a9fc5](https://github.com/tomasbjerre/git-changelog-lib/commit/a9fc5a30a82c320) Tomas Bjerre *2016-01-31 10:30:24*
**Implementing toString on API mode objects**


[283c2](https://github.com/tomasbjerre/git-changelog-lib/commit/283c20a3ae4b7b6) Tomas Bjerre *2016-01-31 10:10:30*
**Lgging exception in rest client**


[e529a](https://github.com/tomasbjerre/git-changelog-lib/commit/e529afb2a911123) Tomas Bjerre *2016-01-31 08:04:33*

## [1.22](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.22) (2016-01-30)







### Other changes

**Removing accidently added duplicate Gson dependency**


[7fde8](https://github.com/tomasbjerre/git-changelog-lib/commit/7fde8935d59456d) Tomas Bjerre *2016-01-30 17:07:40*

## [1.21](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.21) (2016-01-30)







### Other changes

**Correcting revision logging**

* Resetting Jira and GitHub clients before tests. Was having troubles with the cache not being invalidated between tests. 

[00b37](https://github.com/tomasbjerre/git-changelog-lib/commit/00b37d11101f8d1) Tomas Bjerre *2016-01-30 16:39:21*

## [1.20](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.20) (2016-01-30)







### Other changes

**Including first commit**

* Was excluding it when ZERO_COMMIT constant was used 

[c3da0](https://github.com/tomasbjerre/git-changelog-lib/commit/c3da07c0286855d) Tomas Bjerre *2016-01-30 11:56:15*
**Documentation**


[00384](https://github.com/tomasbjerre/git-changelog-lib/commit/0038494e3452b52) Tomas Bjerre *2016-01-30 09:32:00*
**Adding test cases to GitRepo**


[96719](https://github.com/tomasbjerre/git-changelog-lib/commit/96719f26ed22419) Tomas Bjerre *2016-01-28 19:53:50*
**Updating CHANGELOG.md**

* And Removing generate_changelog.sh 

[7613e](https://github.com/tomasbjerre/git-changelog-lib/commit/7613efc4a5a0833) Tomas Bjerre *2016-01-27 18:51:11*

## [1.19](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.19) (2016-01-27)







### Other changes

**Removing command line code #11**


[a7b20](https://github.com/tomasbjerre/git-changelog-lib/commit/a7b20fdea564ff2) Tomas Bjerre *2016-01-27 18:14:33*
**Changing Jenkins plugin link to point at JenkinsCI**


[d405f](https://github.com/tomasbjerre/git-changelog-lib/commit/d405f4e8ae5f34f) Tomas Bjerre *2015-12-11 07:56:23*
**Adding example template to readme**


[296d8](https://github.com/tomasbjerre/git-changelog-lib/commit/296d811a7ec3492) Tomas Bjerre *2015-12-10 17:12:44*

## [1.18](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.18) (2015-12-08)







### Other changes

**Downgrading JGIT to 3.6.2 to be compatible with its older API**


[25fad](https://github.com/tomasbjerre/git-changelog-lib/commit/25fad813507296c) Tomas Bjerre *2015-12-08 18:44:25*

## [1.17](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.17) (2015-12-05)







### Other changes

**Allowing variables to be extended with custom context:s**


[c7e3a](https://github.com/tomasbjerre/git-changelog-lib/commit/c7e3a39354cb339) Tomas Bjerre *2015-12-05 14:30:21*

## [1.16](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.16) (2015-12-05)







### Other changes

**Peeling references**

* To support annotated tags 

[b818f](https://github.com/tomasbjerre/git-changelog-lib/commit/b818fd6a2f1207c) Tomas Bjerre *2015-12-05 14:01:56*

## [1.15](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.15) (2015-12-04)







### Other changes

**Using JGit to find repo folder correctly**

* And JsonPath 2.1.0 

[2d0c1](https://github.com/tomasbjerre/git-changelog-lib/commit/2d0c1f1102387d0) Tomas Bjerre *2015-12-04 22:09:16*

## [1.14](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.14) (2015-12-01)







### Other changes

**Caching requests to GitHub and Jira**


[2b9b6](https://github.com/tomasbjerre/git-changelog-lib/commit/2b9b6112244d1aa) Tomas Bjerre *2015-12-01 21:34:52*

## [1.13](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.13) (2015-11-23)







### Other changes

**Avoiding crash if GitHub issue cant be found**


[abe71](https://github.com/tomasbjerre/git-changelog-lib/commit/abe713cb069aa1f) Tomas Bjerre *2015-11-23 21:44:23*

## [1.12](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.12) (2015-11-23)







### Other changes

**Integrating with Jira #3**


[30a10](https://github.com/tomasbjerre/git-changelog-lib/commit/30a1095331ded15) Tomas Bjerre *2015-11-23 17:32:23*
**Integrating with GitHub #2**


[45af7](https://github.com/tomasbjerre/git-changelog-lib/commit/45af766856ac703) Tomas Bjerre *2015-11-22 19:51:40*

## [1.11](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.11) (2015-11-21)







### Other changes

**Changing master reference constan, to just master**

* refs/heads/master may not exist, perhaps its refs/remotes/origin/master 

[c655c](https://github.com/tomasbjerre/git-changelog-lib/commit/c655cbd914d06f3) Tomas Bjerre *2015-11-21 15:36:03*
**doc**


[8c95f](https://github.com/tomasbjerre/git-changelog-lib/commit/8c95fd4870872d4) Tomas Bjerre *2015-11-21 14:24:35*

## [1.10](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.10) (2015-11-21)







### Other changes

**Adding test cases for MediaWiki integration**

* Linking readme to screenshots 
* Not encoding html-tags for MediaWiki, to enable setting the toclimit with a tag. 

[c8da3](https://github.com/tomasbjerre/git-changelog-lib/commit/c8da374a92183d2) Tomas Bjerre *2015-11-21 14:18:19*
**Adding example html-template**


[7749d](https://github.com/tomasbjerre/git-changelog-lib/commit/7749ddfa42b4034) Tomas Bjerre *2015-11-20 21:38:13*

## [1.9](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.9) (2015-11-20)







### Other changes

**Adding custom issues correctly #bugfix**


[b5f03](https://github.com/tomasbjerre/git-changelog-lib/commit/b5f03710018a65d) Tomas Bjerre *2015-11-20 21:12:55*
**Better regular expression for extracting readable part of tag name #feature**


[d669a](https://github.com/tomasbjerre/git-changelog-lib/commit/d669ae9e7d00e52) Tomas Bjerre *2015-11-20 20:34:55*

## [1.8](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.8) (2015-11-20)







### Other changes

**Setting default setting of ignore commits regexp #bugfix**


[c2268](https://github.com/tomasbjerre/git-changelog-lib/commit/c226865b64ce01c) Tomas Bjerre *2015-11-20 20:24:08*

## [1.7](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.7) (2015-11-20)







### Other changes

**Using correct reference #bugfix**


[388a3](https://github.com/tomasbjerre/git-changelog-lib/commit/388a3a851f665c1) Tomas Bjerre *2015-11-20 19:08:45*

## [1.6](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.6) (2015-11-20)







### Other changes

**Some more work to get the lib working with Jenkins plugin**


[bfa67](https://github.com/tomasbjerre/git-changelog-lib/commit/bfa6753630efcdb) Tomas Bjerre *2015-11-20 17:03:04*
**Better error if not setting fromRepo**


[f978c](https://github.com/tomasbjerre/git-changelog-lib/commit/f978c6fd9c72233) Tomas Bjerre *2015-11-19 21:21:04*

## [1.5](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.5) (2015-11-19)







### Other changes

**Not loading default settings by default when using API**


[d3e86](https://github.com/tomasbjerre/git-changelog-lib/commit/d3e8656074af987) Tomas Bjerre *2015-11-19 20:56:47*
**updating changelog**


[64f31](https://github.com/tomasbjerre/git-changelog-lib/commit/64f318ca8f10ba8) Tomas Bjerre *2015-11-19 19:46:44*

## [1.4](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.4) (2015-11-19)







### Other changes

**Making sure several custom issue patterns can be set with API**


[49c51](https://github.com/tomasbjerre/git-changelog-lib/commit/49c51f0fa73f35c) Tomas Bjerre *2015-11-19 19:40:27*
**Updating mediawiki screenshot**


[1669b](https://github.com/tomasbjerre/git-changelog-lib/commit/1669b02acf8c9fb) Tomas Bjerre *2015-11-18 21:00:59*
**Adding hasIssue hasLink to readme doc #7**


[fbb45](https://github.com/tomasbjerre/git-changelog-lib/commit/fbb455921921ddc) Tomas Bjerre *2015-11-18 19:41:03*

## [1.3](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.3) (2015-11-18)







### Other changes

**Adding new mediawiki screenshot**


[467d1](https://github.com/tomasbjerre/git-changelog-lib/commit/467d126c87b734e) Tomas Bjerre *2015-11-18 19:17:48*
**Adding booleans to check if link and/or issue exists in issue #7**


[210e9](https://github.com/tomasbjerre/git-changelog-lib/commit/210e963a07b8a7b) Tomas Bjerre *2015-11-18 19:06:00*
**Update README.md**


[49afd](https://github.com/tomasbjerre/git-changelog-lib/commit/49afd799253a777) Tomas Bjerre *2015-11-18 06:55:50*
**doc**


[a0834](https://github.com/tomasbjerre/git-changelog-lib/commit/a083453437d20bd) Tomas Bjerre *2015-11-17 20:29:42*

## [1.2](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.2) (2015-11-17)







### Other changes

**MediaWiki integration #4**


[f4582](https://github.com/tomasbjerre/git-changelog-lib/commit/f45829b2c6a78d7) Tomas Bjerre *2015-11-17 19:27:03*
**doc**


[440ba](https://github.com/tomasbjerre/git-changelog-lib/commit/440ba52998293de) Tomas Bjerre *2015-11-15 15:08:56*
**Adding link to Maven plugin**


[8fe5a](https://github.com/tomasbjerre/git-changelog-lib/commit/8fe5a0b66660e6d) Tomas Bjerre *2015-11-15 14:39:41*
**Doc**


[47748](https://github.com/tomasbjerre/git-changelog-lib/commit/47748d4350e9559) Tomas Bjerre *2015-11-15 13:16:55*
**Changing regexp pattern for github**

* So that it does not match feature issues #feature 

[dd31f](https://github.com/tomasbjerre/git-changelog-lib/commit/dd31f0a16fedfcf) Tomas Bjerre *2015-11-15 12:47:06*
**Better error message if no template specified #feature**


[a599d](https://github.com/tomasbjerre/git-changelog-lib/commit/a599d143f676b92) Tomas Bjerre *2015-11-15 12:42:08*
**Prepare for next release**


[6dd34](https://github.com/tomasbjerre/git-changelog-lib/commit/6dd3418a6dc5b0d) Tomas Bjerre *2015-11-15 11:41:48*

## [1.1](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.1) (2015-11-15)







### Other changes

**Car remove issue from commit message in changelog #feature**


[ec026](https://github.com/tomasbjerre/git-changelog-lib/commit/ec026ad2f25c612) Tomas Bjerre *2015-11-15 11:37:18*
**Improving test output #feature**


[3191a](https://github.com/tomasbjerre/git-changelog-lib/commit/3191a431aff2b20) Tomas Bjerre *2015-11-15 10:58:51*
**Adding generated CHANGELOG.md #feature**

* Correcting faulty precondition check, file output argument could not be set. #bug 

[b3ddd](https://github.com/tomasbjerre/git-changelog-lib/commit/b3ddd5fdad30f41) Tomas Bjerre *2015-11-15 09:48:35*
**Customizing changelog #feature**


[59830](https://github.com/tomasbjerre/git-changelog-lib/commit/59830c6f2a394fe) Tomas Bjerre *2015-11-15 09:24:58*
**Adding script to generate changelog**

* Finding git repo in parent folders correctly #bugfix 

[82be7](https://github.com/tomasbjerre/git-changelog-lib/commit/82be7c398d445bd) Tomas Bjerre *2015-11-15 09:15:38*
**More settings can be set from command line**

* More testing 

[ba9d5](https://github.com/tomasbjerre/git-changelog-lib/commit/ba9d565ddd15d1b) Tomas Bjerre *2015-11-15 08:58:00*

## [1.0](https://github.com/tomasbjerre/git-changelog-lib/releases/tag/1.0) (2015-11-14)







### Other changes

**Doc**


[83a8a](https://github.com/tomasbjerre/git-changelog-lib/commit/83a8a7b2f96ba88) Tomas Bjerre *2015-11-14 19:36:21*
**Test**


[5c548](https://github.com/tomasbjerre/git-changelog-lib/commit/5c54861708099d9) Tomas Bjerre *2015-11-14 19:00:17*
**Work for 1.0**


[051ef](https://github.com/tomasbjerre/git-changelog-lib/commit/051effe72405e78) Tomas Bjerre *2015-11-14 12:14:41*
**Initial commit**


[5aaeb](https://github.com/tomasbjerre/git-changelog-lib/commit/5aaeb907f68915a) Tomas Bjerre *2015-11-14 09:46:23*

    