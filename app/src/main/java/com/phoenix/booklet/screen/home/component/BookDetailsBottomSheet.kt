package com.phoenix.booklet.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun BookDetailsBottomSheet(
    modifier: Modifier = Modifier,
    book: Book,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val context = LocalContext.current

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
                            ReadingStatus.WISHLIST -> MaterialTheme.colorScheme.tertiaryContainer
                            ReadingStatus.READING -> MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                            ReadingStatus.FINISHED -> MaterialTheme.colorScheme.primaryContainer
                            ReadingStatus.ARCHIVED -> MaterialTheme.colorScheme.surfaceVariant
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
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "by ${book.author}" +
                            if(book.translator != null) "\nTranslated by ${book.translator}"
                            else "",
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${book.publishYear} • ${book.publisher}",
                    fontSize = 14.sp
                )

            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Original Release • ${book.releaseYear}",
            fontSize = 12.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = when(book.status) {
                    ReadingStatus.WISHLIST -> "On Wishlist"
                    ReadingStatus.READING -> "Currently Reading"
                    ReadingStatus.FINISHED -> "Completed on ${book.dateFinished?.toHumanReadableDate()}"
                    ReadingStatus.ARCHIVED -> "Archived on ${book.dateFinished?.toHumanReadableDate()}"
                },
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
//                .border(
//                    width = 3.dp,
//                    color = MaterialTheme.colorScheme.surfaceVariant,
//                    shape = RoundedCornerShape(8.dp)
//                )
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = book.description,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(8.dp))
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onClickEdit() },
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Edit Details")
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onClickDelete() },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Delete Book")
        }
    }
}