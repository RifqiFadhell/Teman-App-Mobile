package id.teman.app.common

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import java.util.Collections


fun findMidPoint(source: LatLng, destination: LatLng): LatLng {
    val x1: Double = Math.toRadians(source.latitude)
    val y1: Double = Math.toRadians(source.longitude)
    val x2: Double = Math.toRadians(destination.latitude)
    val y2: Double = Math.toRadians(destination.longitude)
    val Bx = Math.cos(x2) * Math.cos(y2 - y1)
    val By = Math.cos(x2) * Math.sin(y2 - y1)
    val x3: Double = Math.toDegrees(
        Math.atan2(
            Math.sin(x1) + Math.sin(x2),
            Math.sqrt((Math.cos(x1) + Bx) * (Math.cos(x1) + Bx) + By * By)
        )
    )
    var y3 = y1 + Math.atan2(By, Math.cos(x1) + Bx)
    y3 = Math.toDegrees((y3 + 540) % 360 - 180)
    return LatLng(x3, y3)
}

fun splitPathIntoPoints(source: LatLng, destination: LatLng): List<LatLng> {
    var distance: Float = findDistance(source, destination)
    val splitPoints: MutableList<LatLng> = ArrayList()
    splitPoints.add(source)
    splitPoints.add(destination)
    while (distance > 1) {
        val polypathSize = splitPoints.size
        val tempPoints: MutableList<LatLng?> = ArrayList()
        tempPoints.addAll(splitPoints)
        var injectionIndex = 1
        for (i in 0 until polypathSize - 1) {
            val a1 = tempPoints[i]
            val a2 = tempPoints[i + 1]
            splitPoints.add(injectionIndex, findMidPoint(a1!!, a2!!))
            injectionIndex += 2
        }
        distance = findDistance(splitPoints[0], splitPoints[1])
    }
    return splitPoints
}

fun findDistance(source: LatLng, destination: LatLng): Float {
    val srcLoc = Location("srcLoc")
    srcLoc.latitude = source.latitude
    srcLoc.longitude = source.longitude
    val destLoc = Location("destLoc")
    destLoc.latitude = destination.latitude
    destLoc.longitude = destination.longitude
    return srcLoc.distanceTo(destLoc)
}

fun snapToPolyline(mSplitPoints: List<LatLng>, currentLocation: LatLng): LatLng? {
    var snappedLatLng: LatLng? = null
    var mMinorIndexTravelled = 0
    val current = Location("current")
    current.latitude = currentLocation.latitude
    current.longitude = currentLocation.longitude
    var minConfirmCount = 0
    var currentMinDistance = 0f
    var previousMinDistance = 0f
    val distances: MutableList<Float> = ArrayList()
    for (point in mSplitPoints.subList(mMinorIndexTravelled, mSplitPoints.size - 1)) {
        val pointLoc = Location("pointLoc")
        pointLoc.latitude = point.latitude
        pointLoc.longitude = point.longitude
        distances.add(current.distanceTo(pointLoc))
        previousMinDistance = currentMinDistance
        currentMinDistance = Collections.min(distances)
        if (currentMinDistance == previousMinDistance) {
            minConfirmCount++
            if (minConfirmCount > 10) {
                mMinorIndexTravelled += distances.indexOf(currentMinDistance)
                snappedLatLng = mSplitPoints.get(mMinorIndexTravelled)
                break
            }
        }
    }
    return snappedLatLng
}

fun getEdgeIndex(polylines: List<LatLng>, currentLatLng: LatLng): Int {
    val edgeIndex1 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 1.0
    )
    val edgeIndex2 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 2.0
    )
    val edgeIndex6 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 6.0
    )
    val edgeIndex10 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 10.0
    )
    val edgeIndex15 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 15.0
    )
//    val edgeIndexes = polylines.mapIndexed { index, latLng ->
//        PolyUtil.locationIndexOnPath(currentLatLng, polylines, true, index.toDouble())
//    }
    var finalIndex = -1
//    edgeIndexes.forEach { index ->
//        if (index >= 0) {
//            finalIndex = index
//            return@forEach
//        }
//    }

    if (edgeIndex1 >= 0) {
        finalIndex = edgeIndex1
    } else if (edgeIndex2 >= 0) {
        finalIndex = edgeIndex2
    } else if (edgeIndex6 >= 0) {
        finalIndex = edgeIndex6
    } else if (edgeIndex10 >= 0) {
        finalIndex = edgeIndex10
    } else if (edgeIndex15 >= 0) {
        finalIndex = edgeIndex15
    }
    return finalIndex
}

fun getSnapLatLng(polylines: List<LatLng>, currentLatLng: LatLng): LatLng {
    val finalIndex = getEdgeIndex(polylines, currentLatLng)

    if (finalIndex >= 0) {
        val snappedLatLng2 =
            if (finalIndex < polylines.count() - 1) polylines[finalIndex + 1] else currentLatLng

        val snappedLatLng = polylines[finalIndex]

        val distance = SphericalUtil.computeDistanceBetween(snappedLatLng, currentLatLng)
        val heading = SphericalUtil.computeHeading(snappedLatLng2, snappedLatLng)
        val extrapolated = SphericalUtil.computeOffset(snappedLatLng, -distance, heading)

        return LatLng(extrapolated.latitude, extrapolated.longitude)
    }
    return currentLatLng
}

fun findNearestPosition(currentLocation: LatLng, polylines: List<LatLng>): LatLng {
    var minDistance = Double.MAX_VALUE
    var nearestPoint: LatLng? = null

    polylines.forEach {
        val distance = PolyUtil.distanceToLine(currentLocation, it, it)

        if (distance < minDistance) {
            minDistance = distance
            nearestPoint = it
        }
    }
    return nearestPoint ?: currentLocation
}

fun calculateSnappedLatLng(polylines: List<LatLng>, currentLatLng: LatLng): LatLng {
    var minDistance = Double.MAX_VALUE
    var closestPointIndex = 0

    polylines.forEachIndexed { index, latLng ->
        val distance = SphericalUtil.computeDistanceBetween(currentLatLng, latLng)

        if (distance < minDistance) {
            minDistance = distance
            closestPointIndex = index
        }
    }

    return if (closestPointIndex == polylines.size - 1) {
        // meaning closest point at the last index
        val snappedLocation = polylines.get(closestPointIndex)
        snappedLocation
    } else {
        // interpolate point between closest point and next point
        val snappedLocation = SphericalUtil.interpolate(
            polylines[closestPointIndex],
            polylines[closestPointIndex + 1],
            (minDistance / SphericalUtil.computeDistanceBetween(
                polylines[closestPointIndex], polylines[closestPointIndex + 1]
            ))
        )
        snappedLocation
    }
}

fun calculateZoomLevel(distance: Float): Float {
    val maxZoom = 25.0f
    val minZoom = 5.0f
    val maxDistance = 1000.0f
    val minDistance = 100.0f

    val zoomLevel =
        maxZoom - (distance - minDistance) * (maxZoom - minZoom) / (maxDistance - minDistance)

    return minZoom.coerceAtLeast(maxZoom.coerceAtMost(zoomLevel))
}