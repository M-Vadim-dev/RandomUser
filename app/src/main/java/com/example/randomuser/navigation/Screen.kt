package com.example.randomuser.navigation

sealed class Screen(val route: String) {
    object Generate : Screen(GENERATE_ROUTE)

    object List : Screen(LIST_ROUTE) {
        fun createRoute(gender: String?, nat: String?) =
            "list?${NavArgs.GENDER}=${gender ?: NavArgs.DEFAULT_STRING}&${NavArgs.NAT}=${nat ?: NavArgs.DEFAULT_STRING}"
    }

    object Detail : Screen("detail/{${NavArgs.USER_ID}}") {
        fun createRoute(userId: String) = "detail/$userId"
    }

    companion object {
        private const val GENERATE_ROUTE = "generate"
        private const val LIST_ROUTE = "list"
    }
}
