package com.example.swiftride.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class RouteRenderer(
    private val context: Context,
    private val mapView: MapView
) {
    private val polylineManager = PolylineManager(mapView)
    private val markers = mutableListOf<Marker>()
    private var animationJob: Job? = null

    /**
     * Clears all markers and polylines from the map, cancelling animations.
     */
    fun clearAll() {
        animationJob?.cancel()
        polylineManager.removeRoute()
        markers.forEach { mapView.overlays.remove(it) }
        markers.clear()
        removeDriverMarker()
        mapView.invalidate()
    }

    private var driverMarker: Marker? = null

    fun updateDriverMarker(position: GeoPoint, driverName: String) {
        if (driverMarker == null) {
            driverMarker = Marker(mapView).apply {
                icon = createMinimalDriverMarker(context)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                infoWindow = null
            }
            mapView.overlays.add(driverMarker)
        }
        driverMarker?.position = position
        driverMarker?.title = driverName
        mapView.invalidate()
    }

    fun removeDriverMarker() {
        driverMarker?.let {
            mapView.overlays.remove(it)
            driverMarker = null
            mapView.invalidate()
        }
    }

    private fun createMinimalDriverMarker(context: Context, sizeDp: Int = 20): Drawable {
        val density = context.resources.displayMetrics.density
        val px = (sizeDp * density).toInt()
        val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Outer white circle
        paint.color = Color.WHITE
        canvas.drawCircle(px / 2f, px / 2f, px / 2f, paint)

        // Inner blue circle
        paint.color = Color.parseColor("#3A86FF")
        canvas.drawCircle(px / 2f, px / 2f, px / 2f - (2 * density).coerceAtLeast(1f), paint)

        // White center dot
        paint.color = Color.WHITE
        canvas.drawCircle(px / 2f, px / 2f, px / 2f - (6 * density).coerceAtLeast(2f), paint)

        return BitmapDrawable(context.resources, bitmap)
    }

    /**
     * Renders pickup/destination markers, draws the polyline, and centers/zooms camera.
     */
    fun renderRoute(
        pickup: GeoPoint,
        destination: GeoPoint,
        routePoints: List<GeoPoint>,
        animatePolyline: Boolean = true,
        scope: CoroutineScope? = null,
        onRenderFinished: () -> Unit = {}
    ) {
        animationJob?.cancel()
        clearAll()

        // 1. Draw Pickup Marker
        val pickupMarker = Marker(mapView).apply {
            position = pickup
            icon = createMinimalCircleMarker(context)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            infoWindow = null
        }
        mapView.overlays.add(pickupMarker)
        markers.add(pickupMarker)

        // 2. Draw Destination Marker
        val destinationMarker = Marker(mapView).apply {
            position = destination
            icon = createMinimalSquareMarker(context)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            infoWindow = null
        }
        mapView.overlays.add(destinationMarker)
        markers.add(destinationMarker)

        // 3. Draw Polyline (with option to animate)
        if (animatePolyline && scope != null && routePoints.size > 1) {
            val polyline = Polyline(mapView).apply {
                outlinePaint.color = Color.parseColor("#3A86FF")
                outlinePaint.strokeWidth = 10f
                outlinePaint.strokeJoin = Paint.Join.ROUND
                outlinePaint.strokeCap = Paint.Cap.ROUND
                outlinePaint.isAntiAlias = true
            }
            mapView.overlays.add(polyline)
            polylineManager.setActivePolyline(polyline)

            animationJob = scope.launch {
                val totalPoints = routePoints.size
                val steps = 25
                val delayTime = 600L / steps
                for (i in 1..steps) {
                    val progress = i.toFloat() / steps
                    val numPoints = (totalPoints * progress).toInt().coerceIn(1, totalPoints)
                    polyline.setPoints(routePoints.take(numPoints))
                    mapView.invalidate()
                    delay(delayTime)
                }
                // Ensure all points are set
                polyline.setPoints(routePoints)
                mapView.invalidate()
                onRenderFinished()
            }
        } else {
            polylineManager.drawRoute(routePoints)
            onRenderFinished()
        }

        // 4. Zoom Camera to fit both markers and entire polyline
        zoomToRoute(routePoints)
        mapView.invalidate()
    }

    /**
     * Automatically adjusts map camera bounds to fit all route points with padding.
     */
    fun zoomToRoute(points: List<GeoPoint>) {
        if (points.isEmpty()) return

        val boundingBox = BoundingBox.fromGeoPoints(points)
        
        // Expand the bounds by a factor to act as padding (around 20% span)
        val latSpan = boundingBox.latitudeSpan
        val lonSpan = boundingBox.longitudeSpan
        val paddingLat = (latSpan * 0.20).coerceAtLeast(0.002)
        val paddingLon = (lonSpan * 0.20).coerceAtLeast(0.002)

        val paddedBox = BoundingBox(
            boundingBox.latNorth + paddingLat,
            boundingBox.lonEast + paddingLon,
            boundingBox.latSouth - paddingLat,
            boundingBox.lonWest - paddingLon
        )

        if (mapView.width > 0 && mapView.height > 0) {
            mapView.zoomToBoundingBox(paddedBox, true)
        } else {
            mapView.post {
                mapView.zoomToBoundingBox(paddedBox, true)
            }
        }
    }

    private fun createMinimalCircleMarker(context: Context, sizeDp: Int = 16): Drawable {
        val density = context.resources.displayMetrics.density
        val px = (sizeDp * density).toInt()
        val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Outer white circle
        paint.color = Color.WHITE
        canvas.drawCircle(px / 2f, px / 2f, px / 2f, paint)

        // Inner black circle
        paint.color = Color.BLACK
        canvas.drawCircle(px / 2f, px / 2f, px / 2f - (2 * density).coerceAtLeast(1f), paint)

        // White center dot
        paint.color = Color.WHITE
        canvas.drawCircle(px / 2f, px / 2f, px / 2f - (5 * density).coerceAtLeast(2f), paint)

        return BitmapDrawable(context.resources, bitmap)
    }

    private fun createMinimalSquareMarker(context: Context, sizeDp: Int = 16): Drawable {
        val density = context.resources.displayMetrics.density
        val px = (sizeDp * density).toInt()
        val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Outer white square
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, px.toFloat(), px.toFloat(), paint)

        // Inner black square
        paint.color = Color.BLACK
        val border1 = (2 * density).coerceAtLeast(1f)
        canvas.drawRect(border1, border1, px - border1, px - border1, paint)

        // White center square
        paint.color = Color.WHITE
        val border2 = (5 * density).coerceAtLeast(2f)
        canvas.drawRect(border2, border2, px - border2, px - border2, paint)

        return BitmapDrawable(context.resources, bitmap)
    }
}
