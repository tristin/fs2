/*
 * Copyright (c) 2013 Functional Streams for Scala
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fs2
package hashing

import cats.effect.Sync

/** Capability trait that provides hashing.
  *
  * The [[create]] method returns an action that instantiates a fresh `Hash` object.
  * `Hash` is a mutable object that supports incremental computation of hashes. A `Hash`
  * instance should be created for each hash you want to compute.
  */
trait Hashing[F[_]] {
  def create(algorithm: String): F[Hash[F]]
  def md5: F[Hash[F]] = create("MD-5")
  def sha1: F[Hash[F]] = create("SHA-1")
  def sha256: F[Hash[F]] = create("SHA-256")
  def sha384: F[Hash[F]] = create("SHA-384")
  def sha512: F[Hash[F]] = create("SHA-512")

  def hashWith(hash: F[Hash[F]]): Pipe[F, Byte, Byte] =
    source => Stream.eval(hash).flatMap(h => h.hash(source))
}

object Hashing {
  implicit def apply[F[_]](implicit F: Hashing[F]): F.type = F

  implicit def forSync[F[_]: Sync]: Hashing[F] = new Hashing[F] {
    def create(algorithm: String): F[Hash[F]] =
      Hash[F](algorithm)
  }
}
