package com.unitedpay.core.model

import com.unitedpay.core.model.session.UserSessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shared in-memory repository managing UPI transactions across features.
 * Automatically synchronizes with the active user session in UserSessionManager.
 */
object TransactionRepository {

    private val _transactions = MutableStateFlow<List<UpiTransaction>>(UserSessionManager.getCurrentTransactions())
    val transactions: StateFlow<List<UpiTransaction>> = _transactions.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            UserSessionManager.currentSession.collect { session ->
                _transactions.value = session?.transactions ?: emptyList()
            }
        }
    }

    fun addTransaction(transaction: UpiTransaction) {
        UserSessionManager.addTransaction(transaction)
    }

    fun getTransactionById(id: String): UpiTransaction? {
        return _transactions.value.find { it.id == id || it.utrNumber == id }
            ?: _transactions.value.firstOrNull()
    }
}
