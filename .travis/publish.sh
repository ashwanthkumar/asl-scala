#!/usr/bin/env bash

set -ex

sbt +publishSigned
sbt sonatypeReleaseAll

echo "Released"
