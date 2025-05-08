package com.example.checkpoint.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer

@Composable
fun BarChart(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
) {


    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                series(*data.toTypedArray())
            }
        }
    }

    val pinkLineComponent = rememberLineComponent(
        fill = fill(Color(0xFFE64CF0)),
        thickness = 24.dp
    )

    val columnProvider = ColumnCartesianLayer.ColumnProvider.series(pinkLineComponent)

    Column(modifier = modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(columnProvider = columnProvider),
                    startAxis = VerticalAxis.rememberStart(
                        guideline = null
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        guideline = null,
                        valueFormatter = { _, x, _ ->
                            labels.getOrNull(x.toInt()) ?: ""
                        }
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}