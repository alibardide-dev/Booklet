package com.phoenix.booklet.screen.home.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.effect.Crop
import coil3.compose.rememberAsyncImagePainter
import com.phoenix.booklet.R
import com.phoenix.booklet.data.FileResult
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.data.model.ReadingStatus
import com.phoenix.booklet.utils.cacheFileUri
import com.phoenix.booklet.utils.deleteFileFromName
import com.phoenix.booklet.utils.getUriFromName
import com.phoenix.booklet.utils.saveUriAsPhoto
import com.phoenix.booklet.utils.toHumanReadableDate
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

@Composable
fun InsertBookBottomSheet(
    modifier: Modifier = Modifier,
    book: Book? = null,
    onClickClose: () -> Unit,
    onClickSave: (Book) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    var photoUri: Uri? by remember { mutableStateOf(getUriFromName(context, book?.cover)) }
    var name by remember { mutableStateOf(book?.name ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var isTranslated by remember { mutableStateOf(book?.translator != null) }
    var translator by remember { mutableStateOf(book?.translator ?: "") }
    var description by remember { mutableStateOf(book?.description ?: "") }
    var status by remember { mutableStateOf(book?.status ?: ReadingStatus.WISHLIST) }
    var date by remember { mutableStateOf(book?.dateFinished ?: Date(System.currentTimeMillis())) }
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())
    var isPickingDate by remember { mutableStateOf(false) }
    var publisher by remember { mutableStateOf(book?.publisher ?: "") }
    var releaseYear by remember { mutableStateOf(book?.releaseYear ?: "") }
    var publishYear by remember { mutableStateOf(book?.publishYear ?: "") }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.data?.let { uri ->
            photoUri = uri
        }
    }

    /*
    BOOK INFO
     */
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Book Info",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { onClickClose() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.25f)
                    .aspectRatio(2 / 3f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .combinedClickable(
                        onClick = {
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                                .apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    setDataAndType(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        "image/*"
                                    )
                                    putExtra("crop", "true") // NOTE: should be string
                                    putExtra("outputX", 600) // This is needed, editor can't close without these two
                                    putExtra("outputY", 400) // This is needed

                                    putExtra("scale", true)
                                    putExtra("aspectX", 2)
                                    putExtra("aspectY", 3)
                                    putExtra("return-data", true)
                                }
                            pickImageLauncher.launch(intent)
                        },
                        onLongClick = {
                            photoUri = null
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (photoUri != null)
                    Image(
                        painter = rememberAsyncImagePainter(photoUri),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(2 / 3f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                else
                    Icon(
                        painter = painterResource(R.drawable.ic_add_photo),
                        contentDescription = "Add Cover Photo",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
            Spacer(Modifier.width(8.dp))
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Book Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    placeholder = { Text("Author Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        /*
        IS TRANSLATED
         */
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            FilterChip(
                selected = !isTranslated,
                onClick = { isTranslated = false },
                label = { Text("Original Language") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = isTranslated,
                onClick = { isTranslated = true },
                label = { Text("Translated") }
            )
        }
        AnimatedVisibility(isTranslated) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = translator,
                onValueChange = { translator = it },
                placeholder = { Text("Translator Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(120.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(16.dp))
        /*
        READING STATUS
         */
        Text(
            text = "Status",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            FilterChip(
                selected = status == ReadingStatus.WISHLIST,
                onClick = { status = ReadingStatus.WISHLIST },
                label = { Text("Wishlist") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.READING,
                onClick = { status = ReadingStatus.READING },
                label = { Text("Reading") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.FINISHED,
                onClick = { status = ReadingStatus.FINISHED },
                label = { Text("Finished") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.ARCHIVED,
                onClick = { status = ReadingStatus.ARCHIVED },
                label = { Text("Archived") }
            )
        }
        Spacer(Modifier.height(8.dp))
        AnimatedVisibility(
            status == ReadingStatus.FINISHED || status == ReadingStatus.ARCHIVED
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { isPickingDate = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text =
                        when (status) {
                            ReadingStatus.FINISHED -> "Finished at ${date.toHumanReadableDate()}"
                            ReadingStatus.ARCHIVED -> "Archived at ${date.toHumanReadableDate()}"
                            else -> ""
                        },
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        /*
        PUBLISHING INFO
         */
        Text(
            text = "Publishing info",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = publisher,
            onValueChange = { publisher = it },
            placeholder = { Text("Publisher Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = releaseYear,
                onValueChange = { releaseYear = it },
                placeholder = { Text("Release Year") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = publishYear,
                onValueChange = { publishYear = it },
                placeholder = { Text("Publish Year") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                coroutine.launch {
                    val uuid = book?.id ?: UUID.randomUUID()
                    val pathUri = getUriFromName(context, book?.cover)
                    var filePath: String? = book?.cover // Or null
                    if (pathUri != null && pathUri != photoUri) {
                        deleteFileFromName(context, book?.cover)
                        filePath = null
                    }
                    if (photoUri != null && pathUri != photoUri) {
                        val result = saveUriAsPhoto(
                            context = context,
                            uri = photoUri,
                            name = "${uuid}-${System.currentTimeMillis()}"
                        )
                        when (result) {
                            is FileResult.Error -> Unit
                            is FileResult.Success -> filePath = result.filePath
                        }
                    }
                    val book = Book(
                        id = uuid,
                        name = name,
                        author = author,
                        translator = if (isTranslated) translator else null,
                        description = description,
                        publisher = publisher,
                        releaseYear = releaseYear,
                        publishYear = publishYear,
                        cover = filePath,
                        status = status,
                        dateFinished = if (
                            status == ReadingStatus.FINISHED || status == ReadingStatus.ARCHIVED
                        )
                            date
                        else null,
                        dateCreated = book?.dateCreated ?: Date(System.currentTimeMillis()),
                        dateUpdated = Date(System.currentTimeMillis())
                    )
                    isLoading = true
                    onClickSave(book)
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && name.isNotBlank() && author.isNotBlank() && (!isTranslated || translator.isNotBlank()) && publisher.isNotBlank() && releaseYear.isNotBlank()
        ) {
            Crossfade(isLoading) { target ->
                if (target) {
                    CircularProgressIndicator()
                } else {
                    Text("Save Book")
                }
            }
        }

        if (isPickingDate) {
            DatePickerDialog(
                onDismissRequest = { isPickingDate = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            date = Date(datePickerState.selectedDateMillis!!)
                            isPickingDate = false
                        },
                    ) {
                        Text("Ok")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}
