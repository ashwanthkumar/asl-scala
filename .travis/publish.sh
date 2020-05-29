#!/usr/bin/env bash

set -ex

cd ..

sbt +publishSigned
sbt sonatypeReleaseAll

echo "Released"
