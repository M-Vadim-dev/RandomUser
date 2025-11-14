package com.example.randomuser.ui.filter

import com.example.randomuser.R

object Gender {
    const val MALE = "male"
    const val FEMALE = "female"
    const val ANY = ""

    val options = listOf(
        R.string.gender_male to MALE,
        R.string.gender_female to FEMALE,
        R.string.gender_any to ANY,
    )
}