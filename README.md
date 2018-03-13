# catalyst
External Gaming Chat Relay

This work has a copyright and no license of use. Copyright (c) 2018, stormsaurus

See LICENSE.md

## description

Parses chat logs and relays it to an IRC or SMS server.

An IRC bot sits in the channel and relays back to a network of game clients through a mod.

This two way communication would let you participate in chat from outside the game from protocols and devices like IRC, IM, email, SMS, even your cell phone.

More details [here](http://james.io/#/projects) .

## notes

This project is dated and relies on some old libraries.

* MySQL 5.0.51b
* Java 6_06
* Tomcat 6.0.16
* yui 2.5.2
* commons-lang-2.4
* jabsorb 1.3rc1
* slf4j 1.4.2

Generally I do not include binaries and libs in a repo.  But there is a reasonable chance some of these will not be readily available in the future.  So I included each components target build jars, the last four libs above, and some eclipse IDE artifacts to outline how the components depend on each other.

Project is currently retired.
