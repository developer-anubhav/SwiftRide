package com.example.swiftride.map

import android.graphics.Color
import android.graphics.Paint
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

class PolylineManager(private val mapView: MapView) {
    private var activePolyline: Polyline? = null

    /**
     * Draws a smooth route polyline with rounded joins, caps, and anti-aliasing.
     * Automatically removes previous route polylines.
     */
    fun drawRoute(points: List<GeoPoint>, colorHex: String = "#3A86FF", strokeWidth: Float = 10f) {
        removeRoute()

        val polyline = Polyline(mapView).apply {
            setPoints(points)
            outlinePaint.color = Color.parseColor(colorHex)
            outlinePaint.strokeWidth = strokeWidth
            outlinePaint.strokeJoin = Paint.Join.ROUND
            outlinePaint.strokeCap = Paint.Cap.ROUND
            outlinePaint.isAntiAlias = true
        }

        activePolyline = polyline
        mapView.overlays.add(polyline)
        mapView.invalidate()
    }

    /**
     * Sets an externally managed polyline as active and cleans up any existing ones.
     */
    fun setActivePolyline(polyline: Polyline) {
        removeRoute()
        activePolyline = polyline
    }

    /**
     * Removes the active route polyline from the MapView.
     */
    fun removeRoute() {
        activePolyline?.let {
            mapView.overlays.remove(it)
            activePolyline = null
            mapView.invalidate()
        }
    }
}
