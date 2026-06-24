package com.sankalp.marketplace.utils

import com.sankalp.marketplace.di.SESSION_SCOPE
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.scope.Scope

object SessionManager : KoinComponent {
    private var sessionScope: Scope? = null

    fun getOrCreateSessionScope(): Scope {
        if (sessionScope == null || sessionScope!!.closed) {
            sessionScope = getKoin().createScope("session_id", SESSION_SCOPE)
        }
        return sessionScope!!
    }

    fun closeSession() {
        sessionScope?.close()
        sessionScope = null
    }
}
