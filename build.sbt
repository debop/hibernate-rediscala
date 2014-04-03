organization := "com.github.debop"

name := "hibernate-rediscala"

version := "1.1"

scalaVersion := "2.10.4"

unmanagedBase := baseDirectory.value / "lib"

resolvers ++= Seq(
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases"),
    // travis can't access 'https'
    // "rediscala" at "https://github.com/etaty/rediscala-mvn/tree/master/releases",
    "rediscala" at "http://pk11-scratch.googlecode.com/svn/trunk/",
    "jboss" at "http://repository.jboss.org/nexus/content/groups/public"
)

val hibernateVersion = "4.3.4.Final"
val springVersion = "4.0.3.RELEASE"

libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % "2.10.4",
    "com.etaty.rediscala" %% "rediscala" % "1.3",
    "org.hibernate" % "hibernate-entitymanager" % hibernateVersion,
    "org.xerial.snappy" % "snappy-java" % "1.1.0.1",
    "de.ruedigermoeller" % "fst" % "1.53",
    "com.typesafe.akka" %% "akka-actor" % "2.2.3",
    "org.slf4j" % "slf4j-api" % "1.7.6",
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "org.hibernate" % "hibernate-testing" % hibernateVersion % "test",
    "com.zaxxer" % "HikariCP" % "1.3.3" % "test",
    "com.h2database" % "h2" % "1.3.175" % "test",
    "mysql" % "mysql-connector-java" % "5.1.29" % "test",
    "org.scalatest" %% "scalatest" % "2.1.2" % "test",
    "com.github.axel22" % "scalameter_2.10" % "0.4" % "test",
    "org.springframework" % "spring-aop" % springVersion % "test",
    "org.springframework" % "spring-beans" % springVersion % "test",
    "org.springframework" % "spring-context" % springVersion % "test",
    "org.springframework" % "spring-orm" % springVersion % "test",
    "org.springframework" % "spring-test" % springVersion % "test",
    "org.springframework.data" % "spring-data-jpa" % "1.5.1.RELEASE" % "test",
    "joda-time" % "joda-time" % "2.3" % "test",
    "org.joda" % "joda-convert" % "1.5" % "test",
    "junit" % "junit" % "4.11" % "test"
)

compileOrder := CompileOrder.Mixed

javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.7", "-target", "1.7")

javaOptions ++= Seq("-ea", "-server", "-Xms512M", "-Xmx2G", "-XX:MaxPermSize=256M", "-XX:+CMSClassUnloadingEnabled")

scalacOptions ++= Seq("-encoding", "UTF-8", "-target:jvm-1.7")

fork in run := true

fork in Test := true

parallelExecution in Test := false

// http://www.scala-sbt.org/release/docs/Detailed-Topics/Testing#options
testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")