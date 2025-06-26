#!/bin/bash
version=$1
echo "Updating Maven module versions to $version"
mvn versions:set -DnewVersion=$version -DgenerateBackupPoms=false
