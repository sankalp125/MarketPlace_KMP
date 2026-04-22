package com.sankalp.marketplace.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.getWindowSize
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronLeft
import compose.icons.feathericons.ChevronRight

@Composable
fun AdaptiveNavigation(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    content: @Composable () -> Unit
) {
    val windowSize = getWindowSize()
    when (windowSize) {
        WindowSize.Compact  -> CompactNavigation(
            selectedItem   = selectedItem,
            onItemSelected = onItemSelected,
            content        = content
        )
        WindowSize.Medium   -> MediumNavigation(
            selectedItem   = selectedItem,
            onItemSelected = onItemSelected,
            content        = content
        )
        WindowSize.Expanded -> ExpandedNavigation(
            selectedItem   = selectedItem,
            onItemSelected = onItemSelected,
            content        = content
        )
    }
}

// compact Navigation bar for phone
@Composable
private fun CompactNavigation(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    content: @Composable () -> Unit
){
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavItem.entries.forEach { item ->
                    NavigationBarItem(
                        selected = selectedItem == item,
                        onClick = { onItemSelected(item) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    }
}

// medium Navigation Rail for tablet
@Composable
private fun MediumNavigation(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    content: @Composable () -> Unit
){
    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            NavItem.entries.forEach { item ->
                NavigationRailItem(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) },
                    alwaysShowLabel = true,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // Divider
        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

// Expanded Side Navigation drawer for desktop

@Composable
private fun ExpandedNavigation(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    val drawerWidth by animateDpAsState(
        targetValue    = if (isExpanded) 240.dp else 72.dp,
        animationSpec  = tween(durationMillis = 300)
    )

    Row(modifier = Modifier.fillMaxSize()) {

        // ─── Side Drawer ───────────────────────────────────
        Column(
            modifier = Modifier
                .width(drawerWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // Collapse / Expand Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = if (isExpanded) Alignment.CenterEnd else Alignment.Center
            ) {
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) FeatherIcons.ChevronLeft
                        else FeatherIcons.ChevronRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // App Name — sirf expanded mein
            AnimatedVisibility(
                visible = isExpanded,
                enter   = fadeIn() + expandHorizontally(),
                exit    = fadeOut() + shrinkHorizontally()
            ) {
                Text(
                    text     = "MarketPlace",
                    style    = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color    = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nav Items
            NavItem.entries.forEach { item ->
                DrawerNavItem(
                    item       = item,
                    isSelected = selectedItem == item,
                    isExpanded = isExpanded,
                    onClick    = { onItemSelected(item) }
                )
            }
        }

        // Divider
        VerticalDivider(
            modifier  = Modifier.fillMaxHeight(),
            thickness = 1.dp,
            color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

// Drawer Nav Items

@Composable
private fun DrawerNavItem(
    item: NavItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(
                horizontal = if (isExpanded) 16.dp else 0.dp,
                vertical   = 12.dp
            ),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
    ) {
        Icon(
            imageVector        = item.icon,
            contentDescription = item.label,
            tint               = contentColor,
            modifier           = Modifier.size(22.dp)
        )

        // Label — sirf expanded mein
        AnimatedVisibility(
            visible = isExpanded,
            enter   = fadeIn() + expandHorizontally(),
            exit    = fadeOut() + shrinkHorizontally()
        ) {
            Text(
                text     = item.label,
                style    = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color    = contentColor,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}