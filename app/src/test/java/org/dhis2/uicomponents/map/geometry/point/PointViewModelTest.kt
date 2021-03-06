package org.dhis2.uicomponents.map.geometry.point

import com.mapbox.geojson.Point
import org.junit.Before
import org.junit.Test

class PointViewModelTest {

    private lateinit var pointViewModel: org.dhis2.maps.geometry.point.PointViewModel

    @Before
    fun setup() {
        pointViewModel = org.dhis2.maps.geometry.point.PointViewModel()
    }

    @Test
    fun `Should set point`() {
        pointViewModel.setPoint(Point.fromLngLat(0.1, 0.0))
        assert(pointViewModel.lat.get() == "0.0" && pointViewModel.lng.get() == "0.1")
    }

    @Test
    fun `Should get point as string where point is defined`() {
        pointViewModel.setPoint(Point.fromLngLat(0.1, 0.0))
        assert(pointViewModel.getPointAsString() == "[0.1,0.0]")
    }

    @Test
    fun `Should get point as string where point is null`() {
        assert(pointViewModel.getPointAsString() == null)
    }
}
