[![Build Status](https://travis-ci.org/ashwanthkumar/asl-scala.svg?branch=master)](https://travis-ci.org/ashwanthkumar/asl-scala)
# asl-scala

Scala parser for [Amazon States Language](https://states-language.net/spec.html). This library should help you parse an ASL JSON and give you the entities as nice case classes with which you can work in your project.

## Usage
```
libraryDependencies += "in.ashwanthkumar" %% "asl-scala" % aslScalaVersion
```

Current Version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/in.ashwanthkumar/asl-scala_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/in.ashwanthkumar/asl-scala_2.12)

```scala
import in.ashwanthkumar.asl.ASLParser
val stateMachine = ASLParser.parse("...asl in json...")
// do whatever is needed with the stateMachine
```

## Progress
We support parsing the spec with following type of states

- [x] Pass
- [x] Task
- [x] Choice
- [x] Fail
- [ ] Wait
- [x] Succeed
- [ ] Parallel
- [ ] Map

## License

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
