package org.zhian.commander.ui.terminal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.zhian.commander.ui.locale.LocalStrings

enum class ToolbarMode {
    Normal, Modifier, Navigation
}

data class TabState(
    val id: Int,
    val title: String,
    val isActive: Boolean,
    val exitCode: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    onNavigateToSettings: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val focusManager = LocalFocusManager.current

    var menuExpanded by remember { mutableStateOf(false) }
    var toolbarMode by remember { mutableStateOf(ToolbarMode.Normal) }
    var showTabRow by remember { mutableStateOf(false) }
    var tabs by remember { mutableStateOf(listOf(TabState(0, "~", true))) }
    var activeTabId by remember { mutableStateOf(0) }

    val scrollState = rememberLazyListState()
    val isScrollingDown by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 &&
            scrollState.firstVisibleItemScrollOffset > 0
        }
    }

    val toolbarVisible by remember {
        derivedStateOf {
            !isScrollingDown || scrollState.firstVisibleItemIndex == 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Commander") },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.newSession) },
                                onClick = {
                                    val newId = (tabs.maxOfOrNull { it.id } ?: 0) + 1
                                    tabs = tabs.map { it.copy(isActive = false) } +
                                           TabState(newId, "~", true)
                                    activeTabId = newId
                                    showTabRow = true
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.restartSession) },
                                onClick = {
                                    // Restart logic placeholder
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(strings.settings) },
                                onClick = {
                                    menuExpanded = false
                                    onNavigateToSettings()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tab Row
                AnimatedVisibility(visible = showTabRow) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = tabs.indexOfFirst { it.id == activeTabId }.coerceAtLeast(0),
                        edgePadding = 0.dp
                    ) {
                        tabs.forEach { tab ->
                            Tab(
                                selected = tab.id == activeTabId,
                                onClick = {
                                    activeTabId = tab.id
                                    tabs = tabs.map { it.copy(isActive = it.id == tab.id) }
                                },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                tab.isActive -> tab.title
                                                tab.exitCode != null -> when (tab.exitCode) {
                                                    0 -> "✓"
                                                    else -> "⚠"
                                                }
                                                else -> tab.title
                                            }
                                        )
                                        IconButton(
                                            onClick = {
                                                tabs = tabs.filter { it.id != tab.id }
                                                if (tabs.isEmpty()) {
                                                    showTabRow = false
                                                    tabs = listOf(TabState(0, "~", true))
                                                    activeTabId = 0
                                                } else if (tab.id == activeTabId) {
                                                    activeTabId = tabs.first().id
                                                }
                                            },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close tab",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                // Terminal Container
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(200) { index ->
                        Text(
                            text = "Hello World!",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Floating Toolbar & FAB
            AnimatedVisibility(
                visible = toolbarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    tonalElevation = 3.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (toolbarMode) {
                            ToolbarMode.Normal -> {
                                TextField(
                                    value = "",
                                    onValueChange = {},
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Command") }
                                )
                                IconButton(onClick = { toolbarMode = ToolbarMode.Modifier }) {
                                    Icon(
                                        imageVector = Icons.Default.GridView,
                                        contentDescription = "Modifier keys"
                                    )
                                }
                                IconButton(onClick = { toolbarMode = ToolbarMode.Navigation }) {
                                    Icon(
                                        imageVector = Icons.Default.Gamepad,
                                        contentDescription = "Navigation"
                                    )
                                }
                            }
                            ToolbarMode.Modifier -> {
                                IconButton(onClick = { toolbarMode = ToolbarMode.Normal }) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Back"
                                    )
                                }
                                ModifierButton("Ctrl")
                                ModifierButton("Alt")
                                ModifierButton("Shift")
                                ModifierButton("Esc")
                            }
                            ToolbarMode.Navigation -> {
                                IconButton(onClick = { toolbarMode = ToolbarMode.Normal }) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Back"
                                    )
                                }
                                IconButton(onClick = { /* Toggle keyboard */ }) {
                                    Icon(
                                        imageVector = Icons.Default.Keyboard,
                                        contentDescription = "Keyboard"
                                    )
                                }
                                IconButton(onClick = { /* Left */ }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Left"
                                    )
                                }
                                IconButton(onClick = { /* Down */ }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Down"
                                    )
                                }
                                IconButton(onClick = { /* Up */ }) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Up"
                                    )
                                }
                                IconButton(onClick = { /* Right */ }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Right"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
                            onClick = { /* Execute command */ },
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Execute"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModifierButton(text: String) {
    Button(
        onClick = { /* Modifier key logic */ },
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text)
    }
}
