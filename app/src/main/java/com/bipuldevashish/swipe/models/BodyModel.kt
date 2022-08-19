package com.bipuldevashish.swipe.models

import java.io.File

data class BodyModel(
val product_name: String,
val product_type: String,
val price: String,
val tax: String,
val file: List<File>
)
