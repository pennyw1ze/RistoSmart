package com.example.ristosmart.utils

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // If we found a barcode, pass the raw value back
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { value ->
                            onBarcodeDetected(value)
                            // Optional: Stop after first detection to prevent spamming
                            // return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle failure if needed
                }
                .addOnCompleteListener {
                    // Crucial: close the imageProxy so the camera can send the next frame
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}