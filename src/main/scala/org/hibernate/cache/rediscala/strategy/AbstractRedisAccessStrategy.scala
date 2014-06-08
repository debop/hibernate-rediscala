package org.hibernate.cache.rediscala.strategy

import org.hibernate.cache.rediscala.regions.RedisTransactionalDataRegion
import org.hibernate.cache.spi.access.SoftLock
import org.hibernate.cfg.Settings


abstract class AbstractRedisAccessStrategy[T <: RedisTransactionalDataRegion](val region: T, val settings: Settings) {

  def putFromLoad(key: Any, value: Any, txTimestamp: Long, version: Any): Boolean =
    putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled)

  def putFromLoad(key: Any, value: Any, txTimestamp: Long, version: Any, minimalPutOverride: Boolean): Boolean

  def lockRegion: SoftLock = null

  def unlockRegion(lock: SoftLock) {
    region.clear()
  }

  def remove(key: Any) {
    region.remove(key)
  }

  def removeAll() {
    region.evictAll()
  }

  def evict(key: Any) {
    region.remove(key)
  }

  def evictAll() {
    region.evictAll()
  }
}
