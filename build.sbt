name := "hibernate-rediscala"

version := "1.0"

scalaVersion := "2.10.4"

val hibernateVersion = "4.3.4.Final"
val springVersion = "4.0.2.RELEASE"

libraryDependencies ++= Seq(
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

resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    "typesafe" at "http://repo.typesafe.com/typesafe/releases",
    "rediscala" at "https://github.com/etaty/rediscala-mvn/tree/master/releases",
    "jboss" at "http://repository.jboss.org/nexus/content/groups/public"
)