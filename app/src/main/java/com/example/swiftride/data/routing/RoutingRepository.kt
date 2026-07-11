package com.example.swiftride.data.routing

import org.osmdroid.util.GeoPoint

class RoutingRepository(
    private val openRouteService: RoutingService,
    private val graphHopperService: RoutingService,
    private val osrmService: RoutingService,
    private val mockService: RoutingService
) {
    suspend fun getRoute(
        start: GeoPoint,
        end: GeoPoint,
        provider: RouteProvider = RoutingConfig.activeProvider
    ): RouteResult {
        return when (provider) {
            RouteProvider.OSRM -> {
                val result = osrmService.getRoute(start, end)
                if (result is RouteResult.Error) {
                    // Fallback to Mock if OSRM is unavailable or fails
                    mockService.getRoute(start, end)
                } else {
                    result
                }
            }
            RouteProvider.OPENROUTE_SERVICE -> {
                val result = openRouteService.getRoute(start, end)
                if (result is RouteResult.Error && result.errorType == RouteErrorType.API_UNAVAILABLE) {
                    // Fallback to Mock if API is not configured or unavailable
                    mockService.getRoute(start, end)
                } else {
                    result
                }
            }
            RouteProvider.GRAPH_HOPPER -> {
                val result = graphHopperService.getRoute(start, end)
                if (result is RouteResult.Error && result.errorType == RouteErrorType.API_UNAVAILABLE) {
                    // Fallback to Mock if API is not configured or unavailable
                    mockService.getRoute(start, end)
                } else {
                    result
                }
            }
            RouteProvider.MOCK -> {
                mockService.getRoute(start, end)
            }
        }
    }
}
