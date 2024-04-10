package com.vivokey.sparkactions.presentation.digital_card.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun VCardManagementCoreFields(
    modifier: Modifier = Modifier,
    firstName: String?,
    lastName: String?,
    title: String,
    org: String,
    note: String,
    imageBitmap: ImageBitmap? = null,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onOrgChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onImageSelected: () -> Unit,
) {

    Card(
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {

            val (firstNameEntry, lastNameEntry, titleEntry, orgEntry, noteEntry, imageButton) = createRefs()

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        onImageSelected()
                    }
                    .constrainAs(imageButton) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(titleEntry.top)
                    },
            ) {
                imageBitmap?.let { bitmap ->
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(120.dp)
                            .clip(CircleShape),
                        bitmap = bitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                if (imageBitmap == null) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(120.dp)
                            .clip(CircleShape),
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            VCardEntry(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(firstNameEntry) {
                        top.linkTo(parent.top)
                        start.linkTo(imageButton.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                capitalizeFirst = true,
                value = firstName ?: "",
                label = "First name"
            ) { value ->
                onFirstNameChange(value)
            }

            VCardEntry(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(lastNameEntry) {
                        top.linkTo(firstNameEntry.bottom)
                        start.linkTo(imageButton.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                value = lastName ?: "",
                capitalizeFirst = true,
                label = "Last name"
            ) { value ->
                onLastNameChange(value)
            }

            VCardEntry(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(titleEntry) {
                        top.linkTo(lastNameEntry.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                value = title,
                label = "Title",
                capitalizeFirst = true,
                onValueChange = { value ->
                    onTitleChange(value)
                }
            )

            VCardEntry(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(orgEntry) {
                        top.linkTo(titleEntry.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                value = org,
                capitalizeFirst = true,
                label = "Org",
                onValueChange = { value ->
                    onOrgChange(value)
                }
            )

            VCardEntry(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(noteEntry) {
                        top.linkTo(orgEntry.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                value = note,
                capitalizeFirst = true,
                isMultiline = true,
                label = "Note",
                onValueChange = { value ->
                    onNoteChange(value)
                }
            )
        }
    }
}