package com.LingTH.fridge.barcode

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.LingTH.fridge.ui.theme.MyApplicationTheme

import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Scanner : ComponentActivity() {
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Start the camera if permission is granted
                setContent {
                    MyApplicationTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            CameraPreview(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            } else {
                Log.e("CameraPermission", "Camera permission denied")
            }
        }

        // Request camera permission
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}

@Composable
fun CameraPreview( modifier: Modifier = Modifier) {
    // Use AndroidView to create a PreviewView
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                // You can set up any additional properties for the PreviewView here
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier.fillMaxSize()
    ) { previewView ->
        startCamera(previewView, previewView.context)
    }
}

private fun startCamera(previewView: PreviewView, context: Context) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder()

            .build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        preview.surfaceProvider = previewView.surfaceProvider

        val resolutionSelectorBuilder = ResolutionSelector.Builder().apply {
            setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            setResolutionStrategy(
                ResolutionStrategy(
                    Size(1280, 720), // Desired resolution
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            )
        }
        val imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(resolutionSelectorBuilder.build())
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .setOutputImageRotationEnabled(true)
            .build()
        Log.d("CameraSetup", "Setting up image analysis")
        val camera = cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, preview, imageAnalysis)
        val cameraControl = camera.cameraControl
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context),   ImageAnalyzer(context, cameraControl))

        Log.d("CameraSetup", "Camera successfully bound to lifecycle")
    }, ContextCompat.getMainExecutor(context))
}



private class ImageAnalyzer(private val context: Context,private val cameraControl: CameraControl,) : ImageAnalysis.Analyzer {
    private var isProcessingBarcode = false
    fun Context.goToNextPage(targetActivity: Class<*>, extras: Map<String, String> = emptyMap()) {
        val intent = Intent(this, targetActivity)
        for ((key, value) in extras) {
            intent.putExtra(key, value)
        }
        startActivity(intent)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        Log.d("ImageAnalyzer", "Analyzing image...")
        Log.d(
            "ImageAnalyzer",
            "ImageProxy: width=${imageProxy.width}, height=${imageProxy.height}, format=${imageProxy.format}"
        )
        if (isProcessingBarcode) {
            imageProxy.close()  // ปิด imageProxy ทันทีหากกำลังประมวลผล
            return
        }
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            Log.d("ImageAnalyzer", "Image analyzed")


            // Barcode scanning logic integrated here
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_AZTEC,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E,
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_PDF417,
                    Barcode.FORMAT_DATA_MATRIX,
                    Barcode.FORMAT_CODE_39,
                    Barcode.FORMAT_CODE_93,
                    Barcode.FORMAT_CODE_128,
                    Barcode.FORMAT_CODABAR,
                    Barcode.FORMAT_ITF,
                )
                .enableAllPotentialBarcodes() // Optional
                .build()

            val scanner = BarcodeScanning.getClient(options)
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    // Debug: พิมพ์บาร์โค้ดทั้งหมดที่ detect ได้
                    barcodes.forEach {
                        Log.d(
                            "DEBUG_SCAN",
                            "Detected: rawValue=${it.rawValue}, format=${it.format}"
                        )
                    }

                    // กรองเฉพาะบาร์โค้ดที่มี rawValue ที่ใช้งานได้จริง
                    val barcode = barcodes.firstOrNull {
                        val value = it.rawValue
                        value != null &&
                                value.isNotBlank() &&
                                value.matches(Regex("^[0-9A-Za-z\\-]{6,30}$")) // <-- ปรับ regex ตามที่คุณใช้จริง
                    }

                    val barcodeValue = barcode?.rawValue

                    if (barcodeValue != null && !isProcessingBarcode) {
                        isProcessingBarcode = true
                        Log.d("Barcode", "✅ Valid barcode: $barcodeValue")

                        CoroutineScope(Dispatchers.Main).launch {
                            context.goToNextPage(Add::class.java, mapOf("barcode" to barcodeValue))
                        }
                    } else {
                        Log.d("Barcode", "❌ No valid barcode or already processing")
                        cameraControl.setZoomRatio(1.0f)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Barcode", "Barcode scanning failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                    // อย่าปล่อยให้ isProcessingBarcode กลับเป็น false หากกำลังเปลี่ยนหน้าอยู่
                    // ถ้าอยากให้สแกนได้อีกครั้งหลังกลับมา ให้ reset flag ที่หน้าต่อไปแทน
                }
        } else {
            Log.e("ImageAnalyzer", "Media image is null")
            imageProxy.close()
        }
    }
}
