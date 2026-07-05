package com.luckypdf.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luckypdf.app.ui.theme.ACCENT_PRIMARY
import com.luckypdf.app.ui.theme.ACCENT_SECONDARY
import com.luckypdf.app.ui.theme.ACCENT_TERTIARY
import com.luckypdf.app.ui.theme.BG_BASE
import com.luckypdf.app.ui.theme.BG_PANEL
import com.luckypdf.app.ui.theme.BG_ELEVATED
import com.luckypdf.app.ui.theme.TEXT_PRIMARY
import com.luckypdf.app.ui.theme.TEXT_SECONDARY

private val heroGradient = Brush.linearGradient(listOf(ACCENT_PRIMARY, ACCENT_TERTIARY))

@Composable
fun LuckyPdfApp(modifier: Modifier = Modifier) {
  Surface(modifier = modifier.fillMaxSize(), color = BG_BASE) {
    LuckyPdfHomeScreen()
  }
}

@Composable
private fun LuckyPdfHomeScreen() {
  Scaffold(
    containerColor = BG_BASE,
    topBar = {
      HomeTopBar()
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {},
        containerColor = ACCENT_PRIMARY,
        contentColor = Color.White
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "New PDF")
      }
    },
    bottomBar = {
      HomeBottomBar()
    }
  ) { contentPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      item {
        HeroCard()
      }
      item {
        SectionHeader(title = "Quick Tools")
      }
      item {
        ToolRow()
      }
      item {
        SectionHeader(title = "Recent Files")
      }
      items(recentDocuments) { document ->
        DocumentCard(document)
      }
    }
  }
}

@Composable
private fun HomeTopBar() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(BG_BASE)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(ACCENT_PRIMARY),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.FolderOpen,
          contentDescription = "LuckyPDF logo",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
      Column(modifier = Modifier.padding(start = 12.dp)) {
        Text("LuckyPDF", style = MaterialTheme.typography.titleLarge, color = TEXT_PRIMARY)
        Text("Smart document toolkit", style = MaterialTheme.typography.bodySmall, color = TEXT_SECONDARY)
      }
    }

    IconButton(onClick = {}) {
      Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = TEXT_PRIMARY)
    }
  }
}

@Composable
private fun HeroCard() {
  Card(
    colors = CardDefaults.cardColors(containerColor = BG_PANEL),
    shape = RoundedCornerShape(20.dp),
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text("Welcome to LuckyPDF", style = MaterialTheme.typography.headlineSmall, color = TEXT_PRIMARY)
      Text(
        "Scan, merge, compress, annotate, and share PDFs with a polished dark interface.",
        style = MaterialTheme.typography.bodyMedium,
        color = TEXT_SECONDARY,
        lineHeight = 20.sp
      )
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(96.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(heroGradient)
      )
    }
  }
}

private data class ToolItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val toolItems = listOf(
  ToolItem("Scan", Icons.Default.CameraAlt),
  ToolItem("Merge", Icons.Default.UploadFile),
  ToolItem("Share", Icons.Default.Share),
  ToolItem("Star", Icons.Default.Star)
)

@Composable
private fun ToolRow() {
  LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
    items(toolItems) { item ->
      Card(
        colors = CardDefaults.cardColors(containerColor = BG_PANEL),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.size(width = 100.dp, height = 120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          horizontalAlignment = Alignment.Start
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Brush.linearGradient(listOf(ACCENT_PRIMARY, ACCENT_SECONDARY))),
            contentAlignment = Alignment.Center
          ) {
            Icon(imageVector = item.icon, contentDescription = item.label, tint = Color.White)
          }
          Text(item.label, style = MaterialTheme.typography.titleSmall, color = TEXT_PRIMARY)
        }
      }
    }
  }
}

private data class DocumentItem(val title: String, val subtitle: String)

private val recentDocuments = listOf(
  DocumentItem("Project Report.pdf", "12 pages · 2.1 MB"),
  DocumentItem("Invoice bundle.pdf", "8 pages · 1.4 MB"),
  DocumentItem("Presentation notes.pdf", "5 pages · 900 KB")
)

@Composable
private fun DocumentCard(document: DocumentItem) {
  Card(
    colors = CardDefaults.cardColors(containerColor = BG_PANEL),
    shape = RoundedCornerShape(18.dp),
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(document.title, style = MaterialTheme.typography.titleMedium, color = TEXT_PRIMARY)
        Text(document.subtitle, style = MaterialTheme.typography.bodySmall, color = TEXT_SECONDARY)
      }
      IconButton(onClick = {}) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Share file", tint = TEXT_PRIMARY)
      }
    }
  }
}

@Composable
private fun SectionHeader(title: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(title, style = MaterialTheme.typography.headlineSmall, color = TEXT_PRIMARY)
    Text("See all", style = MaterialTheme.typography.labelMedium, color = ACCENT_PRIMARY)
  }
}

@Composable
private fun HomeBottomBar() {
  Surface(
    color = BG_PANEL,
    tonalElevation = 8.dp,
    shadowElevation = 8.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Ready to create", style = MaterialTheme.typography.bodyMedium, color = TEXT_SECONDARY)
      IconButton(onClick = {}) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = ACCENT_PRIMARY)
      }
    }
  }
}
