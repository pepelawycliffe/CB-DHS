package com.example.cb_dhs.fhir

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.fhir.datacapture.UrlResolver
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.get
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

class ReferenceUrlResolver(val context: Context) : UrlResolver {
    private fun getLogicalIdFromFhirUrl(url: String, resourceType: ResourceType): String {
        return url.substringAfter("${resourceType.name}/").substringBefore("/")
    }

    override suspend fun resolveBitmapUrl(url: String): Bitmap? {
        val logicalId = getLogicalIdFromFhirUrl(url, ResourceType.Binary)
        return try {
            val binary = FhirApplication.fhirEngine(context).get<Binary>(logicalId)
            BitmapFactory.decodeByteArray(binary.data, 0, binary.data.size)
        } catch (e: ResourceNotFoundException) {
            Timber.e(e)
            null
        }
    }
}