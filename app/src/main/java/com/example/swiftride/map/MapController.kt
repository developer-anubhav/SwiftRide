package com.example.swiftride.map

import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint

class MapController(private val mapView: MapView) {
    fun centerOn(geoPoint: GeoPoint, zoomLevel: Double? = null) {
        mapView.controller.setCenter(geoPoint)
        if (zoomLevel != null) {
            mapView.controller.setZoom(zoomLevel)
        }
    }
}
