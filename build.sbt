name := "hibernate-rediscala"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
    "com.etaty.rediscala" % "rediscala_2.10" % "1.3",
    "org.slf4j" % "slf4j-api" % "1.7.6",
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "com.zaxxer" % "HikariCP" % "1.3.3"
)

resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    "typesafe" at "http://repo.typesafe.com/typesafe/releases",
    "rediscala" at "https://github.com/etaty/rediscala-mvn/raw/master/releases",
    "jboss" at "http://repository.jboss.org/nexus/content/groups/public"
)