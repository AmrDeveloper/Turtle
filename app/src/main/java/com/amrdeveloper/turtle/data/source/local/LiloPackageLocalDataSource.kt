/*
 * MIT License
 *
 * Copyright (c) 2022 AmrDeveloper (Amr Hesham)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.amrdeveloper.turtle.data.source.local

import com.amrdeveloper.turtle.data.LiloPackage
import com.amrdeveloper.turtle.data.source.LiloPackageDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LiloPackageLocalDataSource(
    private val liloPackageDao: LiloPackageDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LiloPackageDataSource {

    override suspend fun loadLiloPackages(): Result<List<LiloPackage>> =
        withContext(dispatcher) {
            return@withContext try {
                Result.success(liloPackageDao.loadLiloPackages())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun insertLiloPackage(liloPackage: LiloPackage): Result<Long> =
        withContext(dispatcher) {
            return@withContext try {
                Result.success(liloPackageDao.insert(liloPackage))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateLiloPackage(liloPackage: LiloPackage): Result<Int> =
        withContext(dispatcher) {
            return@withContext try {
                Result.success(liloPackageDao.update(liloPackage))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteLiloPackage(liloPackage: LiloPackage): Result<Int> =
        withContext(dispatcher) {
            return@withContext try {
                Result.success(liloPackageDao.delete(liloPackage))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteAllLiloPackage(): Result<Int> =
        withContext(dispatcher) {
            return@withContext try {
                Result.success(liloPackageDao.deleteAll())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}