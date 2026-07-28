package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldPrimary
import com.example.util.IntentUtils

@Composable
fun EmergencyFloatingCallButton(
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = {
                IntentUtils.makePhoneCall(context)
            },
            containerColor = GoldPrimary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(16.dp)
                .testTag("floating_call_button")
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Emergency Call Owner",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
