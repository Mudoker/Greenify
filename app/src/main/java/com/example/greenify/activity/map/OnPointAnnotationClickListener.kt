package com.example.greenify.activity.map

import com.mapbox.maps.plugin.annotation.generated.PointAnnotation

interface OnPointAnnotationClickListener {
    fun onPointAnnotationClick(annotation: PointAnnotation)
}