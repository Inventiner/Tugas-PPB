package com.example.mymoneynotes

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.example.mymoneynotes.ui.theme.MyMoneyNotesTheme
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.text.font.FontWeight

enum class FilterType { Pengeluaran, Pemasukan }

data class Transaction(
    val id: UUID = UUID.randomUUID(),
    val type: FilterType,
    val category: String,
    val amount: Double,
    val date: LocalDate = LocalDate.now(),
    val description: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showBackButton: Boolean,
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
    )
}

fun generateInitialDummyData(): List<Transaction> {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val lastMonth = today.minusMonths(1)

    return listOf(
        // Pengeluaran
        Transaction(type = FilterType.Pengeluaran, category = "Food", amount = 15750.0, date = today, description = "Makan Siang"),
        Transaction(type = FilterType.Pengeluaran, category = "Transport", amount = 15500.0, date = today),
        Transaction(type = FilterType.Pengeluaran, category = "Food", amount = 8200.0, date = yesterday),
        Transaction(type = FilterType.Pengeluaran, category = "Bills", amount = 55000.0, date = lastMonth.withDayOfMonth(5), description = "tagihan listrik"),
        Transaction(type = FilterType.Pengeluaran, category = "Entertainment", amount = 35000.0, date = lastMonth.withDayOfMonth(15), description = "Nonton"),
        Transaction(type = FilterType.Pengeluaran, category = "Food", amount = 45300.0, date = lastMonth.withDayOfMonth(20), description = "Groceries"),

        // Pemasukan
        Transaction(type = FilterType.Pemasukan, category = "Salary", amount = 250000.0, date = lastMonth.withDayOfMonth(1), description = "Uang jajan"),
        Transaction(type = FilterType.Pemasukan, category = "Freelance", amount = 35000.0, date = today.minusDays(10)),
        Transaction(type = FilterType.Pemasukan, category = "Other", amount = 50000.0, date = yesterday, description = "Hadiah")
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val generateDummyData = true

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMoneyNotesTheme {
                var showAddScreen by remember { mutableStateOf(false) }
                val transactions = remember { mutableStateListOf<Transaction>().apply{
                    if (generateDummyData) {
                        addAll(generateInitialDummyData())
                    }
                }}
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        AppTopBar(
                            title = "DuITS",
                            showBackButton = showAddScreen,
                            onNavigateBack = { showAddScreen = false } // Navigate back action
                        )
                    },
                    floatingActionButton = {
                        if (!showAddScreen) {
                            FloatingActionButton(onClick = {
                                showAddScreen = true
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
                            }
                        }
                    }
                ) { innerPadding ->
                    if (showAddScreen) {
                        AddTransactionScreen(
                            modifier = Modifier
                                .padding(innerPadding),
                            onAddTransaction = { newTransaction ->
                                transactions.add(newTransaction)
                                showAddScreen = false
                            },
                            onNavigateBack = {
                                showAddScreen = false
                            }
                        )
                    } else {
                        HomeScreen(
                            modifier = Modifier
                                .padding(innerPadding),
                            transactions = transactions
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onAddTransaction: (Transaction) -> Unit
) {
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf(FilterType.Pengeluaran) }
    var amountString by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) } // State for date
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val categories = remember { categoriesColors.keys.toList() }
    var isCategoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()) }

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePickerDialog = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(), contentAlignment = Alignment.Center
        ){
            Text("Add New Transaction", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(10.dp))

        // Tipe Transaksi
        Text("Transaction Type:", style = MaterialTheme.typography.labelLarge)
        Row(
            Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterType.entries.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { selectedType = type }
                ) {
                    RadioButton(
                        selected = (selectedType == type),
                        onClick = { selectedType = type }
                    )
                    Text(text = type.name)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Kategori
        ExposedDropdownMenuBox(
            expanded = isCategoryExpanded,
            onExpandedChange = { isCategoryExpanded = !isCategoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(0.9f)
            )
            ExposedDropdownMenu(
                expanded = isCategoryExpanded,
                onDismissRequest = { isCategoryExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            isCategoryExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Tanggal
        OutlinedTextField(
            value = selectedDate.format(dateFormatter),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            trailingIcon = {
                IconButton(onClick = { showDatePickerDialog = true }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Select Date")
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable { showDatePickerDialog = true }
        )
        Spacer(Modifier.height(16.dp))

        // Jumlah
        OutlinedTextField(
            value = amountString,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                    amountString = newValue
                }
            },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        Spacer(Modifier.height(16.dp))

        // Deskripsi
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(modifier=Modifier.weight(1f), onClick = onNavigateBack) {
                Text("Cancel")
            }
            Spacer(modifier=Modifier.width(16.dp))
            Button(modifier=Modifier.weight(1f), onClick = {
                val amount = amountString.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    Toast.makeText(context, "Please enter a valid positive amount", Toast.LENGTH_SHORT).show()
                } else if (selectedCategory.isBlank()){
                    Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show()
                }
                else {
                    val newTransaction = Transaction(
                        type = selectedType,
                        category = selectedCategory,
                        amount = amount,
                        date = selectedDate,
                        description = description.takeIf { it.isNotBlank() }
                    )
                    onAddTransaction(newTransaction)
                }
            }) { Text("Save Transaction") }
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, transactions: List<Transaction>) {
    val primaryFilters = listOf(FilterType.Pengeluaran, FilterType.Pemasukan)
    var selectedPrimaryFilter by remember { mutableStateOf(FilterType.Pengeluaran) }
    var selectedPieSlice by remember { mutableStateOf<Pie?>(null) }

    val chartInfo by remember(transactions, selectedPrimaryFilter) {
        derivedStateOf {
            val filteredTransactions = transactions.filter { it.type == selectedPrimaryFilter }
            val overallTotal = filteredTransactions.sumOf { it.amount }

            val pieData = filteredTransactions
                .groupBy { it.category }
                .mapValues { (_, transactionsInCategory) ->
                    transactionsInCategory.sumOf { it.amount }
                }
                .filter { it.value > 0 }
                .map { (category, totalAmount) ->
                    Pie(
                        label = category,
                        data = totalAmount,
                        color = getColorForCategory(category)
                    )
                }
            Pair(pieData, overallTotal)
        }
    }
    val chartData = chartInfo.first
    val totalForFilter = chartInfo.second

    val detailDateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault()) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp).padding(bottom = 60.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SingleChoiceSegmentedButtonRow( modifier = Modifier
            ) {
                primaryFilters.fastForEachIndexed { index, filterType ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = primaryFilters.size
                        ),
                        onClick = {
                            selectedPrimaryFilter = filterType
                            selectedPieSlice = null
                        },
                        selected = filterType == selectedPrimaryFilter,
                        icon = {}
                    ) {
                        Text(filterType.name)
                    }
                }
            }
        }
        if (chartData.isNotEmpty()) {
            PieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.35f)
                    .padding(16.dp),
                data = chartData.map { pie ->
                    pie.copy(selected = pie == selectedPieSlice)
                },
                style = Pie.Style.Stroke(width = 30.dp),
                onPieClick = { clickedPie ->
                    selectedPieSlice = if (selectedPieSlice?.label == clickedPie.label) {
                        null
                    } else {
                        clickedPie
                    }
                },
                selectedScale = 1.1f
            )
        } else {
            Box(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp), contentAlignment = Alignment.Center) {
                Text("No ${selectedPrimaryFilter.name} data available!")
            }
        }
        Crossfade(targetState = selectedPieSlice, label = "DetailsTransition") { targetSlice ->
            if (targetSlice == null) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Total ${selectedPrimaryFilter.name}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = currencyFormatter.format(totalForFilter),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }

                    if (transactions.any { it.type == selectedPrimaryFilter }) {
                        Text(
                            text = "All ${selectedPrimaryFilter.name}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                        HorizontalDivider()
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(
                                transactions.filter { it.type == selectedPrimaryFilter }
                                    .sortedByDescending { it.date },
                                key = { it.id }
                            ) { transaction ->
                                TransactionListItem(
                                    transaction = transaction,
                                    dateFormatter = detailDateFormatter,
                                    currencyFormatter = currencyFormatter
                                )
                                HorizontalDivider()
                            }
                        }
                    } else {
                         Spacer(modifier = Modifier.weight(1f))
                    }
                }
            } else {
                val category = targetSlice.label
                val categoryTotal = targetSlice.data
                val percentage = if (totalForFilter > 0) (categoryTotal / totalForFilter) * 100 else 0.0

                val categoryTransactions = remember(transactions, selectedPrimaryFilter, category) {
                    transactions.filter { it.type == selectedPrimaryFilter && it.category == category }
                        .sortedByDescending { it.date }
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = category.toString(),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "${currencyFormatter.format(categoryTotal)} (${String.format("%.1f", percentage)}%)",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Text(
                        text = "Transactions in $category",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    HorizontalDivider()

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(categoryTransactions, key = { it.id }) { transaction ->
                            TransactionListItem(
                                transaction = transaction,
                                dateFormatter = detailDateFormatter,
                                currencyFormatter = currencyFormatter
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

val categoriesColors: Map<String, Color> = mapOf(
    "Food"          to Color(0xFFF44336),
    "Transport"     to Color(0xFF2196F3),
    "Bills"         to Color(0xFFFF9800),
    "Salary"        to Color(0xFF4CAF50),
    "Freelance"     to Color(0xFF009688),
    "Groceries"     to Color(0xFF8BC34A),
    "Entertainment" to Color(0xFF9C27B0),
    "Other"         to Color(0xFF607D8B),
)

private val categoryColorCache = mutableMapOf<String, Color>()
val fallbackColor = Color.Gray
fun getColorForCategory(category: String): Color {
    return categoryColorCache.getOrPut(category) {
        categoriesColors[category] ?: fallbackColor
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    dateFormatter: DateTimeFormatter,
    currencyFormatter: NumberFormat
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(end = 8.dp)) {
            Text(
                text = transaction.description ?: transaction.category,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Text(
                text = "Category: ${transaction.category}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = transaction.date.format(dateFormatter),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = currencyFormatter.format(transaction.amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    val generateDummyData = true
    MyMoneyNotesTheme {
        var showAddScreen by remember { mutableStateOf(false) }
        val transactions = remember { mutableStateListOf<Transaction>().apply{
            if (generateDummyData) {
                addAll(generateInitialDummyData())
            }
        }}
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopBar(
                    title = "DuITS",
                    showBackButton = showAddScreen,
                    onNavigateBack = { showAddScreen = false } // Navigate back action
                )
            },
            floatingActionButton = {
                if (!showAddScreen) {
                    FloatingActionButton(onClick = {
                        showAddScreen = true
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
                    }
                }
            }
        ) { innerPadding ->
            if (showAddScreen) {
                AddTransactionScreen(
                    modifier = Modifier
                        .padding(innerPadding),
                    onAddTransaction = { newTransaction ->
                        transactions.add(newTransaction)
                        showAddScreen = false
                    },
                    onNavigateBack = {
                        showAddScreen = false
                    }
                )
            } else {
                HomeScreen(
                    modifier = Modifier
                        .padding(innerPadding),
                    transactions = transactions
                )
            }
        }
    }
}