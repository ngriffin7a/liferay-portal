#!/bin/sh
    git remote show vsingleton >>/dev/null 2>>/dev/null
    if test $? != 0; then
        git remote add vsingleton https://github.com/vsingleton/liferay-portal.git
    fi
    git fetch vsingleton tck3
    git cherry-pick 71d8c824a5c6
    export ANT_OPTS="-Xms1024m -Xmx3072m -Djava.awt.headless=true"
    ant -Dtck.home=$TCK_HOME -f build-test-tck.xml prepare-session-timeout
