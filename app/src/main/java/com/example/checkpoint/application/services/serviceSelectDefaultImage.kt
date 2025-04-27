package com.example.checkpoint.application.services

fun selectDefaultImageUrl(subscriptionName: String): String {
    val firstLetter = subscriptionName.firstOrNull()?.uppercaseChar() ?: return "DEFAULT_URL"

    return when (firstLetter) {
        'A' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e485300238639b432/view?project=67f11f87002b613f4e14"
        'B' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e485a00198482c0cb/view?project=67f11f87002b613f4e14"
        'C' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e485f001b711f5761/view?project=67f11f87002b613f4e14"
        'D' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e4864000071bf7825/view?project=67f11f87002b613f4e14"
        'E' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e486b0009f8ad7139/view?project=67f11f87002b613f4e14"
        'F' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e486f0039797d0fc4/view?project=67f11f87002b613f4e14"
        'G' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48730033f43d17bb/view?project=67f11f87002b613f4e14"
        'H' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48770010f5bc8f45/view?project=67f11f87002b613f4e14"
        'I' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e487b002afcc89dd2/view?project=67f11f87002b613f4e14"
        'J' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e488000370c073593/view?project=67f11f87002b613f4e14"
        'K' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48840027fe11bebe/view?project=67f11f87002b613f4e14"
        'L' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48880023f3d002b0/view?project=67f11f87002b613f4e14"
        'M' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e488d001bf32cfde8/view?project=67f11f87002b613f4e14"
        'N' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e4891003b8c63f5c6/view?project=67f11f87002b613f4e14"
        'O' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e489e00228358cd8c/view?project=67f11f87002b613f4e14"
        'P' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48a30036861913b3/view?project=67f11f87002b613f4e14"
        'Q' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48a90022f036a179/view?project=67f11f87002b613f4e14"
        'R' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48ae0025f9e605b2/view?project=67f11f87002b613f4e14"
        'S' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e4a5e00276b31dfa9/view?project=67f11f87002b613f4e14"
        'T' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48b2001080bb89b1/view?project=67f11f87002b613f4e14"
        'U' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48b600087bbb858a/view?project=67f11f87002b613f4e14"
        'V' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48ba0017f24f73c8/view?project=67f11f87002b613f4e14"
        'W' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48be001378abb4e1/view?project=67f11f87002b613f4e14"
        'X' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48c200147ebbd059/view?project=67f11f87002b613f4e14"
        'Y' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48c8003bf2529cc2/view?project=67f11f87002b613f4e14"
        'Z' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48cd001d8a1adbf1/view?project=67f11f87002b613f4e14"
        'Ñ' -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48990028748860c4/view?project=67f11f87002b613f4e14"
        else -> "https://fra.cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/680e48f40032f62126fe/view?project=67f11f87002b613f4e14"
    }

}