package com.vraj.spendwise.ui.inputexpense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vraj.spendwise.R
import com.vraj.spendwise.data.expense.ExpenseCategoryFinder
import com.vraj.spendwise.ui.base.BaseModalBottomSheet
import com.vraj.spendwise.ui.hideBottomSheetWithAnimation
import com.vraj.spendwise.ui.theme.DarkBlue
import com.vraj.spendwise.viewmodel.InputExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionBottomSheet(viewModel: InputExpenseViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val currentCategory by viewModel.currentCategory.collectAsState()
    val showCategorySelectionBottomSheet by viewModel.showCategorySelectionBottomSheet.collectAsState()

    LaunchedEffect(showCategorySelectionBottomSheet) {
        if (showCategorySelectionBottomSheet) sheetState.show() else sheetState.hide()
    }

    if (sheetState.isVisible || showCategorySelectionBottomSheet) {
        BaseModalBottomSheet(
            sheetSate = sheetState,
            onDismiss = { viewModel.setShowCategorySelectionBottomSheet(false) },
            content = {
                CategoryList(
                    selected = currentCategory,
                    onSelect = {
                        hideBottomSheetWithAnimation(sheetState, scope) {
                            viewModel.setCurrentCategory(it)
                            viewModel.setShowCategorySelectionBottomSheet(false)
                        }
                    }
                )
            }
        )
    }
}

@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
    list: List<ExpenseCategoryFinder.Category> = ExpenseCategoryFinder.Category.entries,
    selected: ExpenseCategoryFinder.Category,
    onSelect: (ExpenseCategoryFinder.Category) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp)
            .nestedScroll(rememberNestedScrollInteropConnection())
    ) {
        items(list) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSelect(it) }
                    )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(it.getIcon()),
                        contentDescription = "Category type icon",
                        colorFilter = ColorFilter.tint(DarkBlue),
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = it.getName(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (it == selected) {
                    Image(
                        painter = painterResource(R.drawable.ic_tick),
                        contentDescription = "Selected category tick mark"
                    )
                }
            }
        }
    }
}