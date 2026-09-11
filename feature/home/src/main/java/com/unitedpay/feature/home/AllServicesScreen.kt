package com.unitedpay.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.components.UnitedPayLogo
import com.unitedpay.core.designsystem.components.UnitedSearchField
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedCanvasLight
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite

data class FintechService(
    val id: String,
    val title: String,
    val category: String,
    val icon: ImageVector,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllServicesScreen(
    onBackClick: () -> Unit,
    onServiceClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val allServices = remember {
        listOf(
            // Recharges & Utilities (8)
            FintechService("recharge", "Mobile Recharge", "Bills & Recharges", UnitedIcons.Recharge, "Cashback"),
            FintechService("dth", "DTH / Cable", "Bills & Recharges", UnitedIcons.TvCable),
            FintechService("electricity", "Electricity Bill", "Bills & Recharges", UnitedIcons.Electricity, "Instant"),
            FintechService("fastag", "FASTag Toll", "Bills & Recharges", UnitedIcons.Fastag),
            FintechService("gas", "Piped Gas", "Bills & Recharges", UnitedIcons.Gas),
            FintechService("water", "Water Bill", "Bills & Recharges", UnitedIcons.Water),
            FintechService("broadband", "Broadband Wi-Fi", "Bills & Recharges", UnitedIcons.Broadband),
            FintechService("landline", "Landline Bill", "Bills & Recharges", UnitedIcons.Recharge),

            // Banking & Transfers (8)
            FintechService("check_balance", "Check Balance", "Banking & Transfers", UnitedIcons.BankTemple),
            FintechService("self_transfer", "To Self A/C", "Banking & Transfers", UnitedIcons.SelfTransfer, "Zero Fee"),
            FintechService("bank_transfer", "To Bank A/C", "Banking & Transfers", UnitedIcons.BankTransfer, "IMPS"),
            FintechService("add_money", "Add Money / Lite", "Banking & Transfers", UnitedIcons.Plus),
            FintechService("credit_card_bill", "Credit Card Bill", "Banking & Transfers", UnitedIcons.Cards),
            FintechService("autopay", "UPI Autopay", "Banking & Transfers", UnitedIcons.Autopay),
            FintechService("loan_emi", "Loan EMI", "Banking & Transfers", UnitedIcons.LoanEmi),
            FintechService("insurance", "Insurance Premium", "Banking & Transfers", UnitedIcons.Insurance),

            // Investments & Digital Assets (5)
            FintechService("digital_rupee", "Digital Rupee", "Investments & Wealth", UnitedIcons.Crypto, "RBI e₹"),
            FintechService("digital_gold", "Digital Gold 24K", "Investments & Wealth", UnitedIcons.DigitalGold, "99.9%"),
            FintechService("mutual_funds", "Mutual Funds SIP", "Investments & Wealth", UnitedIcons.MutualFunds),
            FintechService("metro", "Metro QR Ticket", "Investments & Wealth", UnitedIcons.Metro),
            FintechService("municipal_tax", "Municipal Tax", "Investments & Wealth", UnitedIcons.BankTemple)
        )
    }

    val filteredServices = if (searchQuery.isBlank()) {
        allServices
    } else {
        allServices.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UnitedPayLogo(size = 32.dp, showWordmark = false, asCardBadge = true)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("All Services & Hub", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnitedWhite,
                    titleContentColor = UnitedTextPrimary,
                    navigationIconContentColor = UnitedTextPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field with single-line ellipsis placeholder and fixed height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UnitedWhite)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                UnitedSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search recharge, bills, transfers, taxes...",
                    onClear = { searchQuery = "" }
                )
            }

            // Services Grid
            val categories = listOf("Bills & Recharges", "Banking & Transfers", "Investments & Wealth")

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                for (cat in categories) {
                    val catItems = filteredServices.filter { it.category == cat }
                    if (catItems.isNotEmpty()) {
                        item {
                            Text(
                                text = cat.uppercase(),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    // 4-column service grid
                                    val chunked = catItems.chunked(4)
                                    for (row in chunked) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            for (svc in row) {
                                                ServiceGridItem(
                                                    service = svc,
                                                    onClick = { onServiceClick(svc.id) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            // Fill remaining empty spaces in row
                                            repeat(4 - row.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceGridItem(
    service: FintechService,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(width = 56.dp, height = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.title,
                    tint = UnitedMoneyBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
            if (service.badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF00C853))
                        .padding(horizontal = 4.5.dp, vertical = 1.5.dp)
                ) {
                    Text(
                        text = service.badge,
                        color = UnitedWhite,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = service.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = UnitedTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 13.sp
        )
    }
}
