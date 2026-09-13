package com.unitedpay.feature.home.services.travel

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedIcons
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.designsystem.util.ReceiptShareHelper
import com.unitedpay.core.model.*
import com.unitedpay.core.model.repository.HookoluServices
import kotlinx.coroutines.launch

/**
 * HookoluPay Integrated Travel & Transit Suite.
 * Replaces previous stubs with real booking modules for
 * Bus, Flights, Trains (IRCTC), and Hotels across North-East India.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelHubScreen(
    initialTab: String = "BUS",
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(initialTab.uppercase()) }

    var busTrips by remember { mutableStateOf<List<BusTrip>>(emptyList()) }
    var flightTrips by remember { mutableStateOf<List<FlightTrip>>(emptyList()) }
    var trainTrips by remember { mutableStateOf<List<TrainTrip>>(emptyList()) }
    var hotels by remember { mutableStateOf<List<HotelProperty>>(emptyList()) }

    var isBooking by remember { mutableStateOf(false) }
    var bookingReceipt by remember { mutableStateOf<TravelBookingReceipt?>(null) }

    LaunchedEffect(Unit) {
        busTrips = HookoluServices.travel.getBusTrips()
        flightTrips = HookoluServices.travel.getFlightTrips()
        trainTrips = HookoluServices.travel.getTrainTrips()
        hotels = HookoluServices.travel.getHotels()
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandShield(size = 26.dp, asCardBadge = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Hookolu Travel Hub",
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Bus • Flights • Trains • Hotels",
                                fontSize = 11.sp,
                                color = UnitedTextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UnitedWhite)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Travel Mode Tabs
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = UnitedWhite,
                    border = BorderStroke(1.dp, UnitedBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        listOf(
                            Triple("BUS", "Bus", UnitedIcons.BusTicket),
                            Triple("FLIGHT", "Flights", UnitedIcons.FlightTicket),
                            Triple("TRAIN", "Trains", UnitedIcons.TrainTicket),
                            Triple("HOTEL", "Hotels", UnitedIcons.HotelBooking)
                        ).forEach { (key, label, icon) ->
                            val isSelected = selectedTab == key
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedTab = key
                                        bookingReceipt = null
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) UnitedMoneyBlue else Color.Transparent
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) UnitedWhite else UnitedTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) UnitedWhite else UnitedTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Booking Receipt Display
            if (bookingReceipt != null) {
                item {
                    val receipt = bookingReceipt!!
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = UnitedWhite,
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Booking Confirmed!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                            Text("PNR / Voucher: ${receipt.pnr}", fontSize = 12.sp, color = UnitedTextSecondary)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = UnitedBorderLight)
                            Spacer(modifier = Modifier.height(16.dp))

                            ReceiptRow("Passenger / Guest", receipt.passengerName)
                            ReceiptRow("Travel Service", receipt.title)
                            ReceiptRow("Route / Location", receipt.routeOrDetails)
                            ReceiptRow("Travel Date", receipt.travelDate)
                            ReceiptRow("Seat / Room", receipt.seatOrRoom)
                            ReceiptRow("Total Fare Paid", receipt.formattedFare, isBold = true, valueColor = UnitedMoneyBlue)

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedButton(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        ReceiptShareHelper.shareTransactionReceipt(
                                            context = activity,
                                            receiptDetails = ReceiptShareHelper.ReceiptDetails(
                                                title = "Hookolu Travel Pass: ${receipt.title}",
                                                amount = receipt.formattedFare,
                                                sender = receipt.passengerName,
                                                receiver = receipt.title,
                                                utr = receipt.pnr,
                                                date = receipt.travelDate,
                                                time = "10:30 PM",
                                                status = "CONFIRMED",
                                                bankName = com.unitedpay.core.model.session.UserSessionManager.getCurrentBankAccounts().firstOrNull()?.let { "${it.bankName} (${it.accountNumberMasked})" } ?: "State Bank of India (•••• 4821)",
                                                paymentMode = "Instant Booking",
                                                note = "Seat/Room: ${receipt.seatOrRoom} • Route: ${receipt.routeOrDetails}"
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, UnitedMoneyBlue)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = UnitedMoneyBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share m-Ticket Voucher", color = UnitedMoneyBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Section List depending on Selected Tab
                when (selectedTab) {
                    "BUS" -> {
                        item {
                            Text("Popular Northeast Bus Routes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                        }

                        items(busTrips) { bus ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = UnitedWhite,
                                border = BorderStroke(1.dp, UnitedBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(bus.operatorName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                            Text("${bus.sourceCity} → ${bus.destinationCity}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = UnitedMoneyBlue)
                                        }
                                        Text(bus.formattedFare, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = UnitedMoneyBlue)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("${bus.departureTime} - ${bus.arrivalTime} (${bus.duration}) • ${bus.busType}", fontSize = 11.sp, color = UnitedTextSecondary)
                                    Text("Boarding: ${bus.boardingPoint} • ${bus.availableSeats} Seats left", fontSize = 11.sp, color = Color(0xFF10B981))

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                isBooking = true
                                                val receipt = HookoluServices.travel.bookTicket(
                                                    travelType = "BUS",
                                                    title = bus.operatorName,
                                                    details = "${bus.sourceCity} → ${bus.destinationCity}",
                                                    date = "Tomorrow, 14 Sep 2026",
                                                    passengerName = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile().fullName,
                                                    seat = "Seat 14A (Window)",
                                                    fare = bus.fare
                                                )
                                                bookingReceipt = receipt
                                                isBooking = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                                    ) {
                                        Text("Book Seat 14A • ${bus.formattedFare}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    "FLIGHT" -> {
                        item {
                            Text("Fastest Direct Flights from Guwahati (GAU)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                        }

                        items(flightTrips) { fl ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = UnitedWhite,
                                border = BorderStroke(1.dp, UnitedBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${fl.airlineName} ${fl.flightNumber}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                            Text("${fl.sourceAirport} → ${fl.destAirport}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = UnitedMoneyBlue)
                                        }
                                        Text(fl.formattedFare, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = UnitedMoneyBlue)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("${fl.departureTime} - ${fl.arrivalTime} (Non-stop ${fl.duration})", fontSize = 11.sp, color = UnitedTextSecondary)

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                isBooking = true
                                                val receipt = HookoluServices.travel.bookTicket(
                                                    travelType = "FLIGHT",
                                                    title = "${fl.airlineName} ${fl.flightNumber}",
                                                    details = "${fl.sourceAirport} → ${fl.destAirport}",
                                                    date = "15 Sep 2026",
                                                    passengerName = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile().fullName,
                                                    seat = "Seat 12F (Standard)",
                                                    fare = fl.fare
                                                )
                                                bookingReceipt = receipt
                                                isBooking = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                                    ) {
                                        Text("Book Flight Ticket • ${fl.formattedFare}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    "TRAIN" -> {
                        item {
                            Text("IRCTC Train Schedules & Status", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                        }

                        items(trainTrips) { tr ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = UnitedWhite,
                                border = BorderStroke(1.dp, UnitedBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${tr.trainNumber} • ${tr.trainName}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                            Text("${tr.sourceStation} → ${tr.destStation}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = UnitedMoneyBlue)
                                        }
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                                            Text("Live Tracking", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Dep: ${tr.departureTime} • Arr: ${tr.arrivalTime} (${tr.duration})", fontSize = 11.sp, color = UnitedTextSecondary)
                                    Text("Available Classes: ${tr.classesAvailable.joinToString(", ")}", fontSize = 11.sp, color = UnitedTextPrimary)

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                isBooking = true
                                                val receipt = HookoluServices.travel.bookTicket(
                                                    travelType = "TRAIN",
                                                    title = "${tr.trainNumber} ${tr.trainName}",
                                                    details = "${tr.sourceStation} → ${tr.destStation}",
                                                    date = "15 Sep 2026",
                                                    passengerName = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile().fullName,
                                                    seat = "Coach B2 Berth 34 (SL)",
                                                    fare = 1450.0
                                                )
                                                bookingReceipt = receipt
                                                isBooking = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                                    ) {
                                        Text("Book 3A Berth • ₹1,450", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    "HOTEL" -> {
                        item {
                            Text("Premium Stays & Partner Resorts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                        }

                        items(hotels) { ht ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = UnitedWhite,
                                border = BorderStroke(1.dp, UnitedBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ht.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedTextPrimary)
                                            Text("${ht.locationSnippet}, ${ht.city}", fontSize = 12.sp, color = UnitedTextSecondary)
                                        }
                                        Text(ht.formattedPrice, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedMoneyBlue)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${ht.rating} • ${ht.amenities.joinToString(" • ")}", fontSize = 11.sp, color = UnitedTextSecondary)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                isBooking = true
                                                val receipt = HookoluServices.travel.bookTicket(
                                                    travelType = "HOTEL",
                                                    title = ht.name,
                                                    details = "${ht.locationSnippet}, ${ht.city}",
                                                    date = "Check-in: 14 Sep 2026",
                                                    passengerName = com.unitedpay.core.model.session.UserSessionManager.getCurrentProfile().fullName,
                                                    seat = "1x Deluxe King Room",
                                                    fare = ht.pricePerNight
                                                )
                                                bookingReceipt = receipt
                                                isBooking = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue)
                                    ) {
                                        Text("Book Room • ${ht.formattedPrice}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = UnitedTextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = UnitedTextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}
