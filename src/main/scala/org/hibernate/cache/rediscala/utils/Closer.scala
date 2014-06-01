package org.hibernate.cache.rediscala.utils

/**
 * Closer
 * Created by debop on 2014. 3. 30.
 */
object Closer {

  def using[A <: {def close() : Unit}, B](closable: A)(f: A => B): B = {
    try {
      f(closable)
    } finally {
      closable.close()
    }
  }
}
