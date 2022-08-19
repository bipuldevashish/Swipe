package com.bipuldevashish.swipe.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.NonNull
import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


object FileUtils {

    private val AUTHORITY by lazy { "com.intangible.localhost.documents" }

    /**
     * TAG for log messages.
     */
    private const val TAG = "FileUtils"

    /**
     * Set to true to enable logging
     */
    private const val DEBUG = false

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is {AUTHORITY}.
     */
    private fun isLocalStorageDocument(uri: Uri): Boolean {
        return AUTHORITY == uri.authority
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is MediaProvider.
     */

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     *
     * @return The value of the _data column, which is typically a file path.
     */

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG) DatabaseUtils.dumpCursor(cursor)
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     *
     */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {
        if (DEBUG) Log.d(
            "$TAG File -",
            "Authority: " + uri.authority +
                    ", Fragment: " + uri.fragment +
                    ", Port: " + uri.port +
                    ", Query: " + uri.query +
                    ", Scheme: " + uri.scheme +
                    ", Host: " + uri.host +
                    ", Segments: " + uri.pathSegments.toString()
        )
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT


        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri)
            } else if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if (("image" == type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if (("video" == type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if (("audio" == type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getImageUri(context: Context, bitmap: Bitmap?): Uri? {
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "myImage", "")
        return Uri.parse(path)
    }

    @NonNull
    fun prepairFiles(partName: String, fileUri: Uri): MultipartBody.Part? {
        val file = File(fileUri.path)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

}