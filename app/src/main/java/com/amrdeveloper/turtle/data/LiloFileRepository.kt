package com.amrdeveloper.turtle.data

class LiloFileRepository(private val dataSource: LiloFileDataSource) {

    fun getLiloFiles() = dataSource.getLiloFiles()
}
