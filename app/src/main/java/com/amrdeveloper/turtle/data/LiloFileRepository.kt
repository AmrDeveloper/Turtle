package com.amrdeveloper.turtle.data

class LiloFileRepository(private val dataSource: LiloFileDataSource) {

    fun getLiloFiles(keyword: String? = null) = dataSource.getLiloFiles(keyword)
}
