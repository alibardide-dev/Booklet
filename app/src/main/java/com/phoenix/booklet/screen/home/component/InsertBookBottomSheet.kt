package com.phoenix.booklet.screen.home.component

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import coil3.compose.rememberAsyncImagePainter
import com.phoenix.booklet.R
import com.phoenix.booklet.data.model.Book
import com.phoenix.booklet.data.model.ReadingStatus
import com.phoenix.booklet.utils.FileResult
import com.phoenix.booklet.utils.deleteFileFromName
import com.phoenix.booklet.utils.getUriFromName
import com.phoenix.booklet.utils.saveUriAsPhoto
import com.phoenix.booklet.utils.toHumanReadableDate
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

@Composable
internal fun InsertBookBottomSheet(
    modifier: Modifier = Modifier,
    book: Book? = null,
    onDismiss: () -> Unit,
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
    var score by remember { mutableFloatStateOf(book?.score?.toFloat() ?: 0f) }
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

    var step by remember { mutableIntStateOf(0) }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        AnimatedVisibility(visible = step == 0) {
            BookInfo(
                photoUri = photoUri,
                onClickPhoto = {
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
                onLongClickPhoto = { photoUri = null },
                name = name,
                onNameChange = { name = it },
                author = author,
                onAuthorChange = { author = it },
                isTranslated = isTranslated,
                toggleTranslation = { isTranslated = it },
                translator = translator,
                onTranslatorChange = { translator = it }
            )
        }
        AnimatedVisibility(visible = step == 1) {
            BookReview(
                score = score,
                onScoreChange = { score = it },
                description = description,
                onDescriptionChange = { description = it }
            )
        }
        AnimatedVisibility(visible = step == 2) {
            BookStatus(
                status = status,
                onStatusChange = { status = it },
                pickDate = { isPickingDate = true },
                date = date
            )
        }
        AnimatedVisibility(visible = step == 3) {
            BookPublishing(
                publisher = publisher,
                onPublisherChange = { publisher = it },
                releaseYear = releaseYear,
                onReleaseYearChange = { releaseYear = it },
                publishYear = publishYear,
                onPublishYearChange = { publishYear = it }
            )
        }
        Spacer(Modifier.height(16.dp))
        Row {
            Button(
                onClick = {
                    if (step > 0) step--
                    else onDismiss()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                AnimatedVisibility(visible = step == 0) {
                    Text("Cancel")
                }
                AnimatedVisibility(visible = step != 0) {
                    Text("Back")
                }
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (step == 3) {
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
                                translator = translator,
                                score = score.toInt(),
                                description = description,
                                publisher = publisher,
                                releaseYear = releaseYear,
                                publishYear = publishYear,
                                cover = filePath,
                                status = status,
                                isFavorite = book?.isFavorite ?: false,
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
                    } else step++
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading && (step != 0 || name.isNotBlank()),
                shape = RoundedCornerShape(8.dp)
            ) {
                AnimatedVisibility(visible = step < 3) {
                    Text("Next")
                }
                AnimatedVisibility(visible = step == 3) {
                    Text("Save")
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

@Composable
private fun BookInfo(
    photoUri: Uri?,
    onClickPhoto: () -> Unit,
    onLongClickPhoto: () -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    author: String,
    onAuthorChange: (String) -> Unit,
    isTranslated: Boolean,
    toggleTranslation: (Boolean) -> Unit,
    translator: String,
    onTranslatorChange: (String) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Book Info",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
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
                        onClick = { onClickPhoto() },
                        onLongClick = { onLongClickPhoto() }
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
                    onValueChange = { onNameChange(it) },
                    placeholder = { Text("Book Name *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { onAuthorChange(it) },
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
                onClick = { toggleTranslation(false) },
                label = { Text("Original Language") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = isTranslated,
                onClick = { toggleTranslation(true) },
                label = { Text("Translated") }
            )
        }
        AnimatedVisibility(isTranslated) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = translator,
                onValueChange = { onTranslatorChange(it) },
                placeholder = { Text("Translator Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }
    }
}

private val emojis = mapOf(
    0 to "",
    1 to "🤢",
    2 to "😫",
    3 to "😖",
    4 to "😣",
    5 to "😕",
    6 to "😐",
    7 to "🙂",
    8 to "😊",
    9 to "😃",
    10 to "😍"
)

@Composable
private fun BookReview(
    score: Float,
    onScoreChange: (Float) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Book Info",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(8.dp))
            AnimatedVisibility(visible = score >= 1) {
                Text("${emojis[score.toInt()]} $score")
            }
        }
        Spacer(Modifier.height(8.dp))
        Slider(
            value = score,
            onValueChange = { onScoreChange(it) },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { onDescriptionChange(it) },
            placeholder = { Text("Description / Review") },
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(120.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
    }
}

@Composable
private fun BookStatus(
    status: ReadingStatus,
    onStatusChange: (ReadingStatus) -> Unit,
    pickDate: () -> Unit,
    date: Date
) {
    Column(Modifier.fillMaxWidth()) {
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
                onClick = { onStatusChange(ReadingStatus.WISHLIST) },
                label = { Text("Wishlist") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.READING,
                onClick = { onStatusChange(ReadingStatus.READING) },
                label = { Text("Reading") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.FINISHED,
                onClick = { onStatusChange(ReadingStatus.FINISHED) },
                label = { Text("Finished") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = status == ReadingStatus.ARCHIVED,
                onClick = { onStatusChange(ReadingStatus.ARCHIVED) },
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
                    .clickable { pickDate() }
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
    }
}

@Composable
private fun BookPublishing(
    publisher: String,
    onPublisherChange: (String) -> Unit,
    releaseYear: String,
    onReleaseYearChange: (String) -> Unit,
    publishYear: String,
    onPublishYearChange: (String) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Publishing info",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = publisher,
            onValueChange = { onPublisherChange(it) },
            placeholder = { Text("Publisher Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = releaseYear,
                onValueChange = { onReleaseYearChange(it) },
                placeholder = { Text("Release Year") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = publishYear,
                onValueChange = { onPublishYearChange(it) },
                placeholder = { Text("Publish Year") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }
    }
}