package com.unitedpay.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.UnitedBottomBar
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.components.UnitedToast
import com.unitedpay.core.designsystem.theme.*

data class ServiceItem(
    val id: String,
    val title: String,
    val category: String,
    val icon: ImageVector,
    val tint: Color = Color(0xFF0078DF),
    val badge: String? = null
)

/**
 * All Services & Utilities Hub matching the Paytm/PhonePe reference layout in media_1789196486960.jpg:
 * - Floating pill search bar ("Search For Brands" with mic & BETA tag)
 * - 4-column rounded white card sections (Popular Services, Recharge & Bills, Travel & Tickets, Banking, Wealth)
 * - Clean direct vector icons without circular blob borders
 * - Persistent Bottom Bar with active "Services" tab
 */
@Composable
fun AllServicesScreen(
    onBackClick: () -> Unit,
    onServiceClick: (String) -> Unit,
    onNavigateHome: () -> Unit = onBackClick,
    onNavigateCards: () -> Unit = {},
    onNavigateScan: () -> Unit = {},
    onNavigateHistory: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val popularServices = remember {
        listOf(
            ServiceItem("metro", "Metro", "Popular Services", UnitedIcons.Metro),
            ServiceItem("movie", "Movie\nTickets", "Popular Services", UnitedIcons.MovieTicket),
            ServiceItem("digital_gold", "Save in\nGold", "Popular Services", UnitedIcons.DigitalGold, tint = Color(0xFFD97706)),
            ServiceItem("train", "Train\nTickets", "Popular Services", UnitedIcons.TrainTicket),
            ServiceItem("pay_later", "United\nPay Later", "Popular Services", UnitedIcons.PayLater),
            ServiceItem("stocks", "Stocks", "Popular Services", UnitedIcons.StocksBull),
            ServiceItem("offers", "Cashback &\nOffers", "Popular Services", UnitedIcons.CashbackTag, badge = "Offers"),
            ServiceItem("refer", "Refer &\nWin", "Popular Services", UnitedIcons.ReferEarn, badge = "₹100")
        )
    }

    val rechargeAndBills = remember {
        listOf(
            ServiceItem("recharge", "Mobile\nRecharge", "Recharge & Bill Payments", UnitedIcons.Recharge),
            ServiceItem("fastag", "FASTag\nRecharge", "Recharge & Bill Payments", UnitedIcons.Fastag),
            ServiceItem("electricity", "Electricity\nBill", "Recharge & Bill Payments", UnitedIcons.Electricity),
            ServiceItem("insurance", "Insurance /\nLIC", "Recharge & Bill Payments", UnitedIcons.Insurance),
            ServiceItem("dth", "DTH\nRecharge", "Recharge & Bill Payments", UnitedIcons.TvCable),
            ServiceItem("gas", "Book LPG\nCylinder", "Recharge & Bill Payments", UnitedIcons.GasCylinder),
            ServiceItem("loan_emi", "Pay Loan\nEMI", "Recharge & Bill Payments", UnitedIcons.LoanEmi),
            ServiceItem("all_bills", "My All\nBills", "Recharge & Bill Payments", Icons.Default.ReceiptLong)
        )
    }

    val travelAndTickets = remember {
        listOf(
            ServiceItem("flight", "Flight", "Travel & Tickets", UnitedIcons.FlightTicket),
            ServiceItem("bus", "Bus", "Travel & Tickets", UnitedIcons.BusTicket),
            ServiceItem("train", "Train", "Travel & Tickets", UnitedIcons.TrainTicket),
            ServiceItem("hotels", "Hotels", "Travel & Tickets", UnitedIcons.HotelBooking)
        )
    }

    val bankingAndTransfers = remember {
        listOf(
            ServiceItem("bank_transfer", "To Bank\nA/C", "Banking & Transfers", UnitedIcons.BankTransfer),
            ServiceItem("self_transfer", "To Self\nA/C", "Banking & Transfers", UnitedIcons.SelfTransfer),
            ServiceItem("check_balance", "Check\nBalance", "Banking & Transfers", UnitedIcons.BankTemple),
            ServiceItem("autopay", "UPI\nAutopay", "Banking & Transfers", UnitedIcons.Autopay)
        )
    }

    val investmentsAndWealth = remember {
        listOf(
            ServiceItem("mutual_funds", "Mutual\nFunds", "Investments & Wealth", UnitedIcons.MutualFunds),
            ServiceItem("digital_gold", "Digital\nGold 24K", "Investments & Wealth", UnitedIcons.DigitalGold, tint = Color(0xFFD97706)),
            ServiceItem("digital_rupee", "Digital\nRupee (e₹)", "Investments & Wealth", UnitedIcons.Crypto),
            ServiceItem("municipal_tax", "Municipal\nTax", "Investments & Wealth", UnitedIcons.BankTemple)
        )
    }

    val allCategories = remember {
        listOf(
            "Popular Services" to popularServices,
            "Recharge & Bill Payments" to rechargeAndBills,
            "Travel & Tickets" to travelAndTickets,
            "Banking & Transfers" to bankingAndTransfers,
            "Investments & Wealth" to investmentsAndWealth
        )
    }

    Scaffold(
        containerColor = Color(0xFFF4F6FB),
        bottomBar = {
            UnitedBottomBar(
                currentRoute = "services",
                onNavigateHome = onNavigateHome,
                onNavigateCards = onNavigateCards,
                onNavigateScan = onNavigateScan,
                onNavigateHistory = onNavigateHistory,
                onNavigateServices = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Floating Pill Search Bar (Matching Image 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(26.dp),
                            spotColor = Color.Black.copy(alpha = 0.08f),
                            ambientColor = Color.Black.copy(alpha = 0.04f)
                        ),
                    shape = RoundedCornerShape(26.dp),
                    color = UnitedWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = UnitedTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search For Brands",
                                    fontSize = 15.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = UnitedTextPrimary
                                ),
                                cursorBrush = SolidColor(UnitedMoneyBlue),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = UnitedTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            // Mic with BETA tag
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        UnitedToast.info("Voice search is in Beta")
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Search",
                                        tint = UnitedTextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "BETA",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF64748B),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Categorized 4-Column Grids inside Rounded White Cards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for ((categoryTitle, items) in allCategories) {
                    val filteredItems = if (searchQuery.isBlank()) {
                        items
                    } else {
                        items.filter {
                            it.title.replace("\n", " ").contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredItems.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 12.dp, vertical = 16.dp)
                                ) {
                                    // Section Header
                                    Text(
                                        text = categoryTitle,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.padding(start = 6.dp, bottom = 12.dp)
                                    )

                                    // 4-Column Grid
                                    val rows = filteredItems.chunked(4)
                                    for (row in rows) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            for (service in row) {
                                                ServiceGridItem(
                                                    service = service,
                                                    onClick = {
                                                        onServiceClick(service.id)
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            // Fill empty spaces if row has fewer than 4 items
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
    service: ServiceItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(width = 54.dp, height = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = service.title.replace("\n", " "),
                tint = service.tint,
                modifier = Modifier.size(28.dp)
            )

            if (service.badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF00C853))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = service.badge,
                        color = UnitedWhite,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = service.title,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}
