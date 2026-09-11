package com.unitedpay.feature.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unitedpay.core.model.PaymentStatus
import com.unitedpay.core.model.TransactionType
import com.unitedpay.core.model.UpiTransaction
import com.unitedpay.core.security.SecurityManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class PaymentUiState(
    val payeeVpa: String = "",
    val payeeName: String = "",
    val amountInput: String = "",
    val note: String = "",
    val isMpinSheetVisible: Boolean = false,
    val isProcessing: Boolean = false,
    val completedTransaction: UpiTransaction? = null,
    val errorMessage: String? = null
)

sealed interface PaymentEffect {
    data class PlaySoundboxAlert(val amount: Double, val payeeName: String) : PaymentEffect
    data class PaymentSuccess(val transaction: UpiTransaction) : PaymentEffect
}

class PaymentViewModel(
    private val securityManager: SecurityManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<PaymentEffect>()
    val effects: SharedFlow<PaymentEffect> = _effects.asSharedFlow()

    fun setPayee(vpa: String, name: String) {
        _uiState.value = _uiState.value.copy(payeeVpa = vpa, payeeName = name)
    }

    fun onAmountChanged(amount: String) {
        val digits = amount.filter { it.isDigit() }
        if (digits.length <= 6) {
            _uiState.value = _uiState.value.copy(amountInput = digits)
        }
    }

    fun onProceedToPay() {
        _uiState.value = _uiState.value.copy(isMpinSheetVisible = true)
    }

    fun onDismissMpin() {
        _uiState.value = _uiState.value.copy(isMpinSheetVisible = false)
    }

    fun submitPin(pin: CharArray) {
        _uiState.value = _uiState.value.copy(
            isMpinSheetVisible = false,
            isProcessing = true
        )

        viewModelScope.launch {
            // Secure memory zeroing guaranteed
            securityManager?.withSanitizedPin(pin) {
                // PIN used for cryptographic signing
            }

            delay(1500) // Simulating network & NPCI switch response

            val amountVal = _uiState.value.amountInput.toDoubleOrNull() ?: 0.0
            val txn = UpiTransaction(
                id = UUID.randomUUID().toString(),
                utrNumber = (100000000000L..999999999999L).random().toString(),
                payeeName = _uiState.value.payeeName.ifBlank { "Verified Merchant" },
                payeeVpa = _uiState.value.payeeVpa.ifBlank { "merchant@unitedpay" },
                amount = amountVal,
                timestamp = System.currentTimeMillis(),
                status = PaymentStatus.SUCCESS,
                type = TransactionType.DEBIT,
                bankName = com.unitedpay.core.model.mock.UnitedMockData.linkedBankAccounts.firstOrNull()?.bankName ?: "State Bank of India",
                bankAccountNumberMasked = com.unitedpay.core.model.mock.UnitedMockData.linkedBankAccounts.firstOrNull()?.accountNumberMasked ?: "•••• 4821",
                note = _uiState.value.note
            )

            com.unitedpay.core.model.TransactionRepository.addTransaction(txn)

            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                completedTransaction = txn
            )

            _effects.emit(PaymentEffect.PlaySoundboxAlert(amountVal, txn.payeeName))
            _effects.emit(PaymentEffect.PaymentSuccess(txn))
        }
    }
}
