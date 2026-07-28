package com.phoenix.booklet.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import com.phoenix.booklet.R
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.data.model.ReadingStatus
import com.phoenix.booklet.utils.getUriFromName
import com.phoenix.booklet.utils.toHumanReadableDate

@Composable
internal fun BookDetailsBottomSheet(
    modifier: Modifier = Modifier,
    book: Book,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val context = LocalContext.current
    var displayToolbar by remember { mutableStateOf(false) }

    Column (
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (marker, picture, details) = createRefs()
            Box(
                Modifier
                    .constrainAs(marker) {
                        top.linkTo(parent.top)
                        bottom.linkTo(picture.bottom)
                        start.linkTo(parent.start)
                        height = Dimension.fillToConstraints
                    }
                    .width(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        when(book.status) {
                            ReadingStatus.WISHLIST -> colorResource(R.color.wishlist_background)
                            ReadingStatus.READING -> colorResource(R.color.reading_background)
                            ReadingStatus.FINISHED -> colorResource(R.color.finished_background)
                            ReadingStatus.ARCHIVED -> colorResource(R.color.archive_background)
                        }
                    )
            )
            Box(
                modifier = Modifier
                    .constrainAs(picture) {
                        linkTo(parent.top, parent.bottom, bias = 0f)
                        start.linkTo(marker.end, margin = 8.dp)
                    }
                    .fillMaxWidth(.25f)
                    .aspectRatio(2 / 3f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (book.cover != null)
                    AsyncImage(
                        model = getUriFromName(context, book.cover),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(2 / 3f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                    )
                else
                    Icon(
                        painter = painterResource(R.drawable.ic_image),
                        contentDescription = "Add Cover Photo",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
            Column(
                Modifier
                    .constrainAs(details) {
                        top.linkTo(parent.top)
                        start.linkTo(picture.end, margin = 16.dp)
                        end.linkTo(parent.end)
                        height = Dimension.preferredWrapContent
                        width = Dimension.fillToConstraints
                    }
            ) {
                Text(
                    text = book.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                if (book.author.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "by ${book.author}",
                        fontSize = 16.sp
                    )
                }
                if (book.translator.isNotEmpty()) {
                    Text(
                        text = "Translated by ${book.translator}",
                        fontSize = 16.sp
                    )
                }
                if (book.publishYear.isNotEmpty() || book.publisher.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${book.publisher} • ${book.publishYear}",
                        fontSize = 14.sp
                    )
                }
                if (book.releaseYear.isNotEmpty()) {
                    Text(
                        text = "Original Release • ${book.releaseYear}",
                        fontSize = 12.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (book.description.isNotEmpty()) {
            Text(
                text = book.description,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
        }
        Text(
            text = when(book.status) {
                ReadingStatus.WISHLIST -> "On Wishlist"
                ReadingStatus.READING -> "Currently Reading"
                ReadingStatus.FINISHED -> "Completed on ${book.dateFinished?.toHumanReadableDate()}"
                ReadingStatus.ARCHIVED -> "Archived on ${book.dateFinished?.toHumanReadableDate()}"
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color =
                when(book.status) {
                    ReadingStatus.WISHLIST -> colorResource(R.color.wishlist_text)
                    ReadingStatus.READING -> colorResource(R.color.reading_text)
                    ReadingStatus.FINISHED -> colorResource(R.color.finished_text)
                    ReadingStatus.ARCHIVED -> colorResource(R.color.archive_text)
                },
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when(book.status) {
                        ReadingStatus.WISHLIST -> colorResource(R.color.wishlist_background)
                        ReadingStatus.READING -> colorResource(R.color.reading_background)
                        ReadingStatus.FINISHED -> colorResource(R.color.finished_background)
                        ReadingStatus.ARCHIVED -> colorResource(R.color.archive_background)
                    }
                )
                .padding(vertical = 8.dp, horizontal = 12.dp)
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onClickDelete() },
                colors = ButtonDefaults.buttonColors(
                    contentColor = colorResource(R.color.delete_onContainer),
                    containerColor = colorResource(R.color.delete_container)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Delete")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onClickEdit() },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Update")
            }
        }
    }
}