hibernate-rediscala  [![Build Status](https://travis-ci.org/debop/hibernate-rediscala.png)](https://travis-ci.org/debop/hibernate-rediscala) [![Coverage Status](https://coveralls.io/repos/debop/hibernate-rediscala/badge.png?branch=master)](https://coveralls.io/r/debop/hibernate-rediscala?branch=master)
=========

[hibernate](http://www.hibernate.org) (4.2.x.Final, 4.3.x.Final) 2nd level cache using redis server
with [rediscala](https://github.com/etaty/rediscala) 1.3.

[rediscala](https://github.com/etaty/rediscala)
use [akka](http://akka.io/) which provide non-blocking and asynchronous feature.

reduce cache size by [Fast-Serialization](https://github.com/RuedigerMoeller/fast-serialization)
and [snappy-java](https://github.com/xerial/snappy-java). thanks!


### Maven Repository

add dependency
```xml
<dependency>
    <groupId>com.github.debop</groupId>
    <artifactId>hibernate-rediscala</artifactId>
    <version>1.1</version>
</dependency>
```

add repository
```xml
<repositories>
    <repository>
        <id>debop-releases</id>
        <url>https://github.com/debop/debop-maven-repo/raw/master/releases</url>
    </repository>
    <!-- for snapshot -->
    <repository>
        <id>debop-snapshots</id>
        <url>https://github.com/debop/debop-maven-repo/raw/master/snapshots</url>
    </repository>
</repositories>
```

### setup hibernate configuration

setup hibernate configuration.

```scala
// Secondary Cache
props.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, "true")
props.setProperty(AvailableSettings.USE_QUERY_CACHE, "true")
props.setProperty(AvailableSettings.CACHE_REGION_FACTORY, classOf[SingletonRedisRegionFactory].getName)
props.setProperty(AvailableSettings.CACHE_REGION_PREFIX, "hibernate")
props.setProperty(AvailableSettings.GENERATE_STATISTICS, "true")
props.setProperty(AvailableSettings.USE_STRUCTURED_CACHE, "true")
props.setProperty(AvailableSettings.TRANSACTION_STRATEGY, classOf[JdbcTransactionFactory].getName)

props.setProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties")
```

also same configuration for using [Spring Framework](http://spring.io)
or [Spring Data JPA](http://projects.spring.io/spring-data-jpa/).

### redis settings for hibernate-rediscala

sample for hibernate-redis.properties

```ini
 ##########################################################
 #
 # properities for hibernate-redis
 #
 ##########################################################

 # Redis Server for hibernate 2nd cache
 redis.host=localhost
 redis.port=6379

 # redis.timeout=2000
 # redis.password=

 # database for hibernate cache
 # redis.database=0
 redis.database=1

 # hiberante 2nd cache default expiry (seconds)
 redis.expiryInSeconds=120

 # expiry of hibernate.common region (seconds) // hibernate is prefix, region name is common
 redis.expiryInSeconds.hibernate.common=0

 # expiry of hibernate.account region (seconds) // hibernate is prefix, region name is account
 redis.expiryInSeconds.hibernate.account=1200
```

### Setup hibernate entity to use cache

add @org.hibernate.annotations.Cache annotation to Entity class like this

```scala
import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
@SerialVersionUID(5597936606448211014L)
class Item extends Serializable {

    @Id
    @GeneratedValue
    var id: java.lang.Long = _

    var name: String = _

    var description: String = _

}
```

### How to monitor hibernate-cache is running

run "redis-cli monitor" command in terminal. you can see putting cached items, retrieving cached items.

### Sample code

see [HibernateCacheTest.java](https://github.com/debop/hibernate-rediscala/blob/master/src/test/scala/org/hibernate/cache/rediscala/tests/hibernate/HibernateCacheTest.scala) for more usage.

