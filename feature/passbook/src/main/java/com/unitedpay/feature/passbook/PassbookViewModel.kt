package com.unitedpay.feature.passbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unitedpay.core.database.dao.TransactionDao
import com.unitedpay.core.model.TransactionRepository
import com.unitedpay.core.model.UpiTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class PassbookUiState(
    val isLoading: Boolean = false,
    val transactions: List<UpiTransaction> = emptyList(),
    val filterType: String = "ALL",
    val searchQuery: String = ""
)

class PassbookViewModel(
    private val transactionDao: TransactionDao? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PassbookUiState(
            isLoading = false,
            transactions = TransactionRepository.transactions.value
        )
    )
    val uiState: StateFlow<PassbookUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            if (transactionDao != null) {
                transactionDao.getAllTransactions()
                    .map { list -> list.map { it.toDomain() } }
                    .collect { txns ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            transactions = txns.ifEmpty { TransactionRepository.transactions.value }
                        )
                    }
            } else {
                TransactionRepository.transactions.collect { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        transactions = list
                    )
                }
            }
        }
    }
}
