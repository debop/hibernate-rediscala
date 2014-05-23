package org.hibernate.cache.rediscala.client

import java.util.concurrent.TimeUnit
import org.hibernate.cache.rediscala.serializer.SnappyFstCacheEntryFormatter
import org.slf4j.LoggerFactory
import redis.RedisClient
import redis.api.Limit
import redis.commands.TransactionBuilder
import redis.protocol.MultiBulk
import scala.annotation.varargs
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Success


/**
 * Redis-Server 에 Cache 정보를 저장하고 로드하는 Class 입니다.
 * 참고: rediscala 라이브러리를 사용합니다 ( https://github.com/etaty/rediscala )
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 20. 오후 1:14
 */
class HibernateRedisCache(val redis: RedisClient) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    // Cache Value 를 Fast-Serialization 을 이용하면 속도가 5배 정도 빨라지고, Snappy를 이용하여 압축을 수행하면 15배 정도 빨라진다.
    implicit val cacheEntryFormatter = new SnappyFstCacheEntryFormatter[Any]()

    def ping: Future[String] = redis.ping()

    def dbSize: Future[Long] = redis.dbsize()

    /**
     * Cache item 존재 유무 확인
     */
    def exists(region: String, key: String): Future[Boolean] = {
        redis.hexists(region, key)
    }

    /**
     * Get client item
     *
     * @param region           regions name
     * @param key              client key
     * @param expireInSeconds expiration timeout value
     * @return return cached entity, if not exists return null.
     */
    @inline
    def get(region: String, key: String, expireInSeconds: Long = 0): Future[Any] = {

        // 값을 가져오고, 값이 있고, expiration이 설정되어 있다면 갱신합니다.
        val get = redis.hget[Any](region, key)

        // expiration이 설정되어 있다면 갱신합니다.
        get onSuccess {
            case Some(x: Any) =>
                if (expireInSeconds > 0 && !region.contains("UpdateTimestampsCache") && x != null) {
                    val score = System.currentTimeMillis + expireInSeconds * 1000L
                    redis.zadd(regionExpireKey(region), (score, key))
                }
        }

        get.map(x => x.getOrElse(null.asInstanceOf[Any]))
    }

    /**
     * 지정한 region에 있는 모든 캐시 키를 조회합니다.
     */
    def keysInRegion(region: String): Future[Seq[String]] = {
        redis.hkeys(region)
    }

    def keySizeInRegion(region: String): Future[Long] = redis.hlen(region)

    def getAll(region: String): Future[Map[String, Any]] = {
        redis.hgetall[Any](region)
    }

    @varargs
    def multiGet(region: String, keys: String*): Future[Seq[Any]] = {
        redis.hmget[Any](region, keys: _*).map { x => x.flatten }
    }

    def multiGet(region: String, keys: Iterable[String]): Future[Seq[Any]] = {
        redis.hmget[Any](region, keys.toSeq: _*).map { x => x.flatten }
    }

    /**
     * 캐시 항목을 저장합니다.
     * @param region 영역
     * @param key cache key
     * @param value cache value
     * @param expiry expiry
     * @param unit time unit
     * @return if saved return true, else false
     */
    @inline
    def set(region: String,
            key: String,
            value: Any,
            expiry: Long = 0,
            unit: TimeUnit = TimeUnit.SECONDS): Future[Boolean] = {

        val result = redis.hset(region, key, value)

        result onSuccess {
            case true =>
                if (expiry > 0) {
                    val score = System.currentTimeMillis + unit.toSeconds(expiry) * 1000L
                    redis.zadd(regionExpireKey(region), (score, key))
                }
        }
        result
    }

    /**
     * 지정한 영역의 캐시 항목 중 expire 된 것들을 모두 삭제한다.
     * @param region regions name
     */
    @inline
    def expire(region: String): Future[Unit] = Future {
        val regionExpire = regionExpireKey(region)
        val score = System.currentTimeMillis()

        val results = redis.zrangebyscore[String](regionExpire, Limit(0), Limit(score))
        val keysToExpire = Await.result(results, 10 seconds)

        if (keysToExpire != null && keysToExpire.nonEmpty) {
            log.trace(s"cache item들을 expire 시킵니다. regions=$region, keys=$keysToExpire")
            redis.hdel(region, keysToExpire: _*)
            redis.zremrangebyscore(regionExpire, Limit(0), Limit(score))
        }
    }

    /**
     * 캐시 항목을 삭제합니다.
     * @param region regions name
     * @param key cache key to delete
     */
    def delete(region: String, key: String): Future[Long] = {
        redis.hdel(region, key) andThen {
            case Success(x) => redis.zrem(regionExpireKey(region), key)
        }
    }

    @varargs
    def multiDelete(region: String, keys: String*): Future[Boolean] = {
        if (keys == null || keys.isEmpty)
            return Future.successful(false)

        Future {
            redis.hdel(region, keys: _*)
            redis.zrem(regionExpireKey(region), keys: _*)
            true
        }
    }

    def multiDelete(region: String, keys: Iterable[String]): Future[Boolean] = {
        multiDelete(region, keys.toSeq: _*)
    }

    /**
     * 지정한 영역을 삭제합니다.
     * @param region regions name
     */
    def deleteRegion(region: String): Future[Long] = {
        redis.del(region) andThen {
            case Success(x) => redis.del(regionExpireKey(region))
        }
    }

    def flushDb(): Future[Boolean] = {
        log.info(s"Redis DB 전체를 flush 합니다...")
        redis.flushdb()
    }

    /**
     * 지정한 block 을 transaction 을 이용하여 수행합니다.
     */
    def withTransaction(block: TransactionBuilder => Unit): Future[MultiBulk] = {
        val tx = redis.transaction()
        block(tx)
        tx.exec()
    }

    /**
     * 캐시 영역별로 expiration 정보를 가지도록 하는 redis key 값입니다.
     */
    private def regionExpireKey(region: String) = region + ":expire"
}

/**
 * HibernateRedisCache Companion Object
 */
object HibernateRedisCache {

    implicit val akkaSystem = akka.actor.ActorSystem("hibernate-rediscala")

    // Cache expiration 기본 값 (0 이면 expire 하지 않는다)
    val DEFAULT_EXPIRY_IN_SECONDS = 0

    // default resion name
    val DEFAULT_REGION_NAME = "hibernate"

    def apply(redis: RedisClient = RedisClient()): HibernateRedisCache =
        new HibernateRedisCache(redis)
}
