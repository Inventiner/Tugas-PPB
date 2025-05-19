package com.example.currencycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridExtensionItems
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.currencycalculator.ui.theme.CurrencyCalculatorTheme
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class Currency(val code: String, val displayName: String) {
    IDR("IDR", "Indonesian Rupiah"),
    USD("USD", "US Dollar"),
    EUR("EUR", "Euro"),
    JPY("JPY", "Japanese Yen"),
    GBP("GBP", "British Pound"),
    CNY("CNY", "Chinese Yuan"),
    INR("INR", "Indian Rupee"),
    AUD("AUD", "Australian Dollar"),
    CAD("CAD", "Canadian Dollar"),
    CHF("CHF", "Swiss Franc"),
    HKD("HKD", "Hong Kong Dollar"),
    NZD("NZD", "New Zealand Dollar"),
    SGD("SGD", "Singapore Dollar"),
    THB("THB", "Thai Baht"),
    KRW("KRW", "South Korean Won"),
}

val exchangeRatesToUsd = mapOf(
    Currency.USD to 1.0,
    Currency.IDR to 0.000061,
    Currency.EUR to 1.13,
    Currency.JPY to 0.0069,
    Currency.GBP to 1.33,
    Currency.CNY to 0.14,
    Currency.INR to 0.012,
    Currency.AUD to 0.64,
    Currency.CAD to 0.72,
    Currency.CHF to 1.21,
    Currency.HKD to 0.13,
    Currency.NZD to 0.59,
    Currency.SGD to 0.77,
    Currency.THB to 0.030,
    Currency.KRW to 0.00071
)

fun getRateToUsd(currency: Currency): Double = exchangeRatesToUsd[currency] ?: 1.0

enum class ButtonType {
    NUMBER, FUNCTION, EQUALS, DECIMAL, BACKSPACE
}

data class ButtonData(
    val label: String,
    val type: ButtonType
)

enum class ActiveField {
    TOP, BOTTOM
}

fun getCalculatorButtons(): List<ButtonData> {
    return listOf(
        ButtonData("7", ButtonType.NUMBER),
        ButtonData("8", ButtonType.NUMBER),
        ButtonData("9", ButtonType.NUMBER),
        ButtonData("AC", ButtonType.FUNCTION),

        ButtonData("4", ButtonType.NUMBER),
        ButtonData("5", ButtonType.NUMBER),
        ButtonData("6", ButtonType.NUMBER),
        ButtonData("\u232B", ButtonType.BACKSPACE),

        ButtonData("1", ButtonType.NUMBER),
        ButtonData("2", ButtonType.NUMBER),
        ButtonData("3", ButtonType.NUMBER),
        ButtonData("\u00B1", ButtonType.FUNCTION),

        ButtonData("00", ButtonType.NUMBER),
        ButtonData("0", ButtonType.NUMBER),
        ButtonData(".", ButtonType.DECIMAL),
        ButtonData("=", ButtonType.EQUALS)
    )
}

@Composable
fun CalcButton(buttonData: ButtonData, onClick: () -> Unit, modifier: Modifier) {
    val containerColour = when (buttonData.type) {
        ButtonType.FUNCTION, ButtonType.BACKSPACE -> Color(0xFF6B9AC4)
        ButtonType.EQUALS -> Color(0xFFE39774)
        else -> Color(0xFF326273)
    }
    val contentColour = when (buttonData.type) {
        ButtonType.FUNCTION, ButtonType.BACKSPACE, ButtonType.EQUALS -> Color.Black
        else -> Color.White
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(120.dp).fillMaxWidth().border(0.4.dp, color = Color.DarkGray),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColour,
            contentColor = contentColour
        )
    ) {
        Text(
            text = buttonData.label,
            fontSize = 32.sp,
            color = contentColour,
        )
    }
}

fun updateNumericInput(currentValue: String, buttonData: ButtonData): String {
    return when (buttonData.type) {
        ButtonType.NUMBER -> {
            if (currentValue == "0") {
                buttonData.label
            } else {
                val newValue = currentValue + buttonData.label
                if (newValue.length <= 12) newValue else currentValue
            }
        }
        ButtonType.DECIMAL -> {
            if (!currentValue.contains(".")) {
                val newValue = if (currentValue.isEmpty() || currentValue == "-") currentValue + "0." else "$currentValue."
                if (newValue.length <= 12) newValue else currentValue
            } else {
                currentValue
            }
        }
        ButtonType.BACKSPACE -> {
            if (currentValue.isNotEmpty()) {
                currentValue.dropLast(1)
            } else {
                ""
            }
        }
        ButtonType.FUNCTION -> {
            when (buttonData.label) {
                "AC" -> "0"
                "\u00B1" -> {
                    if (currentValue.startsWith("-")) {
                        currentValue.removePrefix("-")
                    } else if (currentValue.isNotEmpty() && currentValue != "0") {
                        "-$currentValue"
                    } else {
                        currentValue
                    }
                }
                else -> currentValue
            }
        }
        else -> currentValue
    }
}

fun calculateConversion(amount: Double, from: Currency, to: Currency): Double {
    if (from == to || !amount.isFinite() || amount == 0.0) return amount

    val rateFromToUsd = getRateToUsd(from)
    val rateToUsd = getRateToUsd(to)

    if (rateToUsd == 0.0) return Double.NaN

    val amountInUsd = amount * rateFromToUsd
    val result = amountInUsd / rateToUsd

    return result
}

fun doubleToRawInputString(value: Double): String {
    if (!value.isFinite()) return "0"
    val df = DecimalFormat("#.###", DecimalFormatSymbols(Locale.US))
    return df.format(value)
}

fun parseFormattedString(formatted: String): Double? {
    if (formatted.isBlank()) return null
    return formatted.replace(",", "").toDoubleOrNull()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyInputRow(
    currencies: List<Currency>,
    selectedCurrency: Currency,
    onCurrencyChange: (Currency) -> Unit,
    displayValue: String,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    interactionSource: MutableInteractionSource,
    onFocusChangedCallback: (Boolean) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
            modifier = Modifier.weight(0.3f)
        ) {
            OutlinedTextField(
                value = selectedCurrency.code,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.width(200.dp)
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text("${currency.code} - ${currency.displayName}") },
                        onClick = {
                            onCurrencyChange(currency)
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = displayValue,
            onValueChange = { },
            modifier = Modifier
                .weight(0.7f)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    val hasFocus = focusState.toString() == "ActiveParent"
                    onFocusChangedCallback(hasFocus)
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { focusRequester.requestFocus() }
                ),
            readOnly = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.End),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            )
        )
    }
}

@Composable
fun Calculator(modifier: Modifier) {
    val currencies = Currency.entries.toList()
    val buttonData = getCalculatorButtons()

    var currencyTop by remember { mutableStateOf(Currency.IDR) }
    var currencyBottom by remember { mutableStateOf(Currency.USD) }
    var topInputString by remember { mutableStateOf("0") }
    var bottomInputString by remember { mutableStateOf("0") }
    var activeField by remember { mutableStateOf(ActiveField.TOP) }

    val topFocusRequester = remember { FocusRequester() }
    val bottomFocusRequester = remember { FocusRequester() }
    val topInteractionSource = remember { MutableInteractionSource() }
    val bottomInteractionSource = remember { MutableInteractionSource() }

    val displayValueTop = topInputString
    val displayValueBottom = bottomInputString

    fun updateField(primaryChanged: ActiveField) {
        if (primaryChanged == ActiveField.TOP) {
            val topValue = parseFormattedString(topInputString)
            if (topValue != null) {
                val convertedValue = calculateConversion(topValue, currencyTop, currencyBottom)
                bottomInputString = doubleToRawInputString(convertedValue)
            } else {
                bottomInputString = "0"
            }
        } else if (primaryChanged == ActiveField.BOTTOM) {
            val bottomValue = parseFormattedString(bottomInputString)
            if (bottomValue != null) {
                val convertedValue = calculateConversion(bottomValue, currencyBottom, currencyTop)
                topInputString = doubleToRawInputString(convertedValue)
            } else {
                topInputString = "0"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Currency Calculator",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.End
        ) {
            CurrencyInputRow(
                currencies = currencies,
                selectedCurrency = currencyTop,
                onCurrencyChange = { newCurrency ->
                    currencyTop = newCurrency
                    if (activeField == ActiveField.TOP) {
                            updateField(ActiveField.TOP)
                    } else {
                    val bottomValue = parseFormattedString(bottomInputString)
                    if (bottomValue != null) {
                        val convertedValue = calculateConversion(bottomValue, currencyBottom, newCurrency)
                        topInputString = doubleToRawInputString(convertedValue)
                    }
                }},
                displayValue = displayValueTop,
                isFocused = activeField == ActiveField.TOP,
                focusRequester = topFocusRequester,
                interactionSource = topInteractionSource,
                onFocusChangedCallback = { isNowFocused ->
                    if (isNowFocused) {
                        activeField = ActiveField.TOP
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- Bottom Input Row ---
            CurrencyInputRow(
                currencies = currencies,
                selectedCurrency = currencyBottom,
                onCurrencyChange = { newCurrency ->
                    currencyBottom = newCurrency
                    if (activeField == ActiveField.BOTTOM) {
                        updateField(ActiveField.BOTTOM)
                    } else {
                        val topValue = parseFormattedString(topInputString)
                        if (topValue != null) {
                            val convertedValue = calculateConversion(topValue, currencyTop, newCurrency)
                            bottomInputString = doubleToRawInputString(convertedValue)
                        }
                    }
                },
                displayValue = displayValueBottom,
                isFocused = activeField == ActiveField.BOTTOM,
                focusRequester = bottomFocusRequester,
                interactionSource = bottomInteractionSource,
                onFocusChangedCallback = { isNowFocused ->
                    if (isNowFocused) {
                        activeField = ActiveField.BOTTOM
                    }
                }
            )
        }
        LazyVerticalGrid (
            columns = GridCells.Fixed(4),
            modifier = modifier.fillMaxWidth(),
            userScrollEnabled = false,
            contentPadding = PaddingValues(0.dp)
        ) {
            gridExtensionItems(items = buttonData) { button ->
                CalcButton(
                    buttonData = button,
                    onClick = {
                        print(activeField)
                        when(activeField) {
                            ActiveField.TOP -> {
                                topInputString = updateNumericInput(topInputString, button)
                                updateField(ActiveField.TOP)
                            }
                            ActiveField.BOTTOM -> {
                                bottomInputString = updateNumericInput(bottomInputString, button)
                                updateField(ActiveField.BOTTOM)
                            }
                        }
                    },
                    modifier = Modifier,
                )
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CurrencyCalculatorTheme {
        Calculator(modifier = Modifier)
    }
}
