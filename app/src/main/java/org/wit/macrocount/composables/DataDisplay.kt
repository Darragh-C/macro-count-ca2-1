package org.wit.macrocount.composables


import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun ProgressBar(title: String, progress: Int, total: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge)
                    Text(text = "$progress/$total", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress.toFloat() / total.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun ProgressBarPreview() {
    ProgressBar("Calories", 20, 100)
}

@Preview
@Composable
fun ColumnWithMPAndroidChart() {
    val macroTotals: List<PieEntry> = listOf(
        PieEntry(25f, "Protein"),
        PieEntry(35f, "Carbs"),
        PieEntry(40f, "Fat")
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        MPAndroidChartComponent(macroTotals, "Macro proportions")
    }
}

@Composable
fun MPAndroidChartComponent(chartEntries: List<PieEntry>,chartLabel: String) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        AndroidView(
            factory = { context ->
                PieChart(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
        ) { chart ->
            configurePieChart(chart, density, chartEntries, chartLabel)
        }
    }
}



private fun configurePieChart(chart: PieChart, density: Float, chartEntries: List<PieEntry>, chartLabel: String) {

    val pieDataSet = PieDataSet(chartEntries, chartLabel)
    pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()
    pieDataSet.valueTextSize = 15f
    pieDataSet.valueTextColor = 1

    val pieData = PieData(pieDataSet)
    chart.data = pieData

    chart.centerText = chartLabel
    chart.animateY(2000)
}