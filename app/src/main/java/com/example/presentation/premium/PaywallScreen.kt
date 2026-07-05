package com.example.presentation.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.util.PreferenceManager

@Composable
fun PaywallScreen(
    prefManager: PreferenceManager,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.WorkspacePremium,
            contentDescription = null,
            tint = Color(0xFFEF6C00),
            modifier = Modifier.size(100.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Trial Expired",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your 5-day trial has ended. Upgrade to Premium to continue your learning journey and access all features.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        PremiumFeatureItem("Unlimited Vocabulary Words")
        PremiumFeatureItem("Unlimited Folders & Quiz")
        PremiumFeatureItem("Full Access to News & Articles")
        PremiumFeatureItem("Priority Support")

        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { 
                prefManager.isPremium = true
                onBackToHome()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Upgrade to Premium - $9.99", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        TextButton(onClick = onBackToHome) {
            Text("Maybe Later", color = Color.Gray)
        }
    }
}

@Composable
fun PremiumFeatureItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF2E7D32))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 16.sp)
    }
}
