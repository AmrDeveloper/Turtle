package com.amrdeveloper.lilo.common

import com.amrdeveloper.lilo.parser.LiloLoc

data class LiloDiagnostic(val loc: LiloLoc, val message: String)