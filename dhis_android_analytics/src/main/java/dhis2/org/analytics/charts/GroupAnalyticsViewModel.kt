package dhis2.org.analytics.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dhis2.org.analytics.charts.data.AnalyticGroup

const val MIN_SIZE_TO_SHOW = 2

class GroupAnalyticsViewModel : ViewModel() {

    private val _chipItems = MutableLiveData<List<AnalyticGroup>>()
    val chipItems: LiveData<List<AnalyticGroup>> = _chipItems

    init {
        fetchAnalyticsGroup()
    }

    fun fetchAnalyticsGroup() {
        val analytics = listOf(
            AnalyticGroup("1", "Group 1"),
            AnalyticGroup("2", "Group 4"),
            AnalyticGroup("3", "Group 6"),
            AnalyticGroup("5", "Group 7"),
            AnalyticGroup("6", "Group 9"),
            AnalyticGroup("11", "Group 12"),
            AnalyticGroup("22", "Group 222"),
            AnalyticGroup("23", "Group 2333"),
            AnalyticGroup("34", "Group 3444")
        )
        _chipItems.value = analytics
    }
}