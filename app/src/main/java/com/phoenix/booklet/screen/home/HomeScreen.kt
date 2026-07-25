package com.phoenix.booklet.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.data.model.ReadingStatus
import com.phoenix.booklet.screen.home.component.BookWidget
import com.phoenix.booklet.screen.home.component.HomeDeleteBottomSheet
import com.phoenix.booklet.screen.home.component.HomeDetailBottomSheet
import com.phoenix.booklet.screen.home.component.HomeInsertBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMotionApi::class
)
@Composable
fun HomeScreen(
    onClickSettings: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    isUpdateAvailable: Boolean,
    isLoading: Boolean,
    books: List<Book>,
    requestInsert: (Book) -> Unit,
    requestUpdate: (Book) -> Unit,
    requestDelete: (List<UUID>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FilterStatus.ALL) }

    val scope = rememberCoroutineScope()
    var dialogType: HomeDialog by remember { mutableStateOf(HomeDialog.None) }
    var topBarStatus: TopBarStatus by remember { mutableStateOf(TopBarStatus.Normal) }
    val selectedBooks = remember { mutableStateListOf<UUID>() }

    BackHandler(topBarStatus == TopBarStatus.Search) {
        searchQuery = ""
        onSearchQueryChange("")
        topBarStatus = TopBarStatus.Normal
    }

    BackHandler(topBarStatus == TopBarStatus.Select) {
        selectedBooks.clear()
        topBarStatus = TopBarStatus.Normal
    }

    AnimatedVisibility(
        visible = dialogType != HomeDialog.None,
        enter = slideInVertically(animationSpec = TweenSpec(easing = EaseInCubic)),
        exit = slideOutVertically(animationSpec = TweenSpec(easing = EaseInCubic)),
    ) {
        if (dialogType is HomeDialog.Details) {
            val id = (dialogType as HomeDialog.Details).id
            val book = remember(id, books) {
                books.first { it.id == id }
            }

            HomeDetailBottomSheet(
                book = book,
                onDismiss = {
                    dialogType = HomeDialog.None
                },
                onEdit = {
                    dialogType = HomeDialog.Update(id)
                },
                onDelete = {
                    dialogType = HomeDialog.Delete(listOf(id))
                }
            )
        }

        if (dialogType == HomeDialog.Insert) {
            HomeInsertBottomSheet(
                onDismiss = {
                    dialogType = HomeDialog.None
                },
                onSave = {
                    scope.launch(Dispatchers.IO) {
                        requestInsert(it)
                    }
                    dialogType = HomeDialog.None
                }
            )
        }

        if (dialogType is HomeDialog.Update) {
            val id = (dialogType as HomeDialog.Update).id
            val book = remember(id, books) {
                books.firstOrNull { it.id == id }
            }

            HomeInsertBottomSheet(
                onDismiss = {
                    dialogType = HomeDialog.None
                },
                onSave = {
                    scope.launch(Dispatchers.IO) {
                        requestUpdate(it)
                    }
                    dialogType = HomeDialog.None
                },
                book = book
            )
        }

        if (dialogType is HomeDialog.Delete) {
            val ids = (dialogType as HomeDialog.Delete).ids

            HomeDeleteBottomSheet(
                onDismiss = {
                    dialogType = HomeDialog.None
                },
                onConfirm = {
                    scope.launch(Dispatchers.IO) {
                        requestDelete(ids)
                        selectedBooks.clear()
                    }
                    dialogType = HomeDialog.None
                },
                size = ids.size,
                isLoading = isLoading
            )

        }
    }

    Scaffold(
        topBar = {
            Crossfade(topBarStatus) { status ->
                when (status) {
                    TopBarStatus.Normal -> {
                        CenterAlignedTopAppBar(
                            title = { Text("Booklet") },
                            navigationIcon = {
                                IconButton(
                                    onClick = { onClickSettings() },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    BadgedBox(
                                        badge = {
                                            if (isUpdateAvailable)
                                                Badge(containerColor = MaterialTheme.colorScheme.error)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Open Settings"
                                        )
                                    }
                                }
                            },
                            actions = {
                                if (books.isNotEmpty())
                                    IconButton(
                                        onClick = { topBarStatus = TopBarStatus.Search },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                    }
                            }
                        )
                    }

                    TopBarStatus.Search -> {
                        CenterAlignedTopAppBar(
                            title = {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it
                                        onSearchQueryChange(it)
                                    },
                                    placeholder = { Text("Search in books") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { topBarStatus = TopBarStatus.Normal },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "Exit Search Mode",
                                    )
                                }
                            }
                        )
                    }

                    TopBarStatus.Select -> {
                        TopAppBar(
                            title = { Text("${selectedBooks.size} books selected") },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        topBarStatus = TopBarStatus.Normal
                                        selectedBooks.clear()
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "Exit Select Mode"
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { dialogType = HomeDialog.Delete(selectedBooks) },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete Selected",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }
                }
            }

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    dialogType = HomeDialog.Insert
                }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Text(
                        text = "Add Book"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        val filterScrollState = rememberScrollState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (books.isNotEmpty() && !isLoading) {
                stickyHeader {
                    AnimatedVisibility(
                        visible = topBarStatus == TopBarStatus.Normal,
                        enter = slideInVertically { -1 },
                        exit = slideOutVertically { -1 }
                    ) {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .horizontalScroll(filterScrollState),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(Modifier.width(16.dp))
                            FilterChip(
                                selected = selectedFilter == FilterStatus.ALL,
                                onClick = { selectedFilter = FilterStatus.ALL },
                                label = { Text("All books") }
                            )
                            VerticalDivider(Modifier.fillMaxHeight())
                            FilterChip(
                                selected = selectedFilter == FilterStatus.WISHLIST,
                                onClick = { selectedFilter = FilterStatus.WISHLIST },
                                label = { Text("Wishlist") }
                            )
                            FilterChip(
                                selected = selectedFilter == FilterStatus.READING,
                                onClick = { selectedFilter = FilterStatus.READING },
                                label = { Text("Reading") }
                            )
                            FilterChip(
                                selected = selectedFilter == FilterStatus.FINISHED,
                                onClick = { selectedFilter = FilterStatus.FINISHED },
                                label = { Text("Finished") }
                            )
                            FilterChip(
                                selected = selectedFilter == FilterStatus.ARCHIVED,
                                onClick = { selectedFilter = FilterStatus.ARCHIVED },
                                label = { Text("Archive") }
                            )
                            Spacer(Modifier.width(16.dp))
                        }
                    }
                }
                items(
                    items = books.filter {
                        when (selectedFilter) {
                            FilterStatus.ALL -> true
                            FilterStatus.WISHLIST -> it.status == ReadingStatus.WISHLIST
                            FilterStatus.READING -> it.status == ReadingStatus.READING
                            FilterStatus.FINISHED -> it.status == ReadingStatus.FINISHED
                            FilterStatus.ARCHIVED -> it.status == ReadingStatus.ARCHIVED
                        }
                    }.filter {
                        (it.name + it.author + it.translator + it.releaseYear + it.publishYear + it.publisher)
                            .contains(searchQuery, ignoreCase = true)
                    },
                    key = { it.id }
                ) { book ->
                    BookWidget(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        book = book,
                        isSelected = selectedBooks.any { book.id == it },
                        onClick = {
                            if (topBarStatus == TopBarStatus.Select) {
                                if (selectedBooks.any { book.id == it }) {
                                    selectedBooks.remove(book.id)
                                    if (selectedBooks.isEmpty())
                                        topBarStatus = TopBarStatus.Normal
                                } else {
                                    selectedBooks.add(book.id)
                                }
                            } else {
                                dialogType = HomeDialog.Details(book.id)
                            }
                        },
                        onLongClick = {
                            selectedBooks.add(book.id)
                            topBarStatus = TopBarStatus.Select
                        }
                    )
                }
            }

            if (books.isEmpty() && !isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = """
                            Oh, you haven't added any book!
                            Click button below, or import books from a backup in settings
                        """.trimIndent(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .animateItem(fadeInSpec = tween(delayMillis = 200)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularWavyProgressIndicator()
                    }
                }
            }
        }
    }
}