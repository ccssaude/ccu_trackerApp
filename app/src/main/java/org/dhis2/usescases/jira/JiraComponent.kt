package org.dhis2.usescases.jira

import dagger.Subcomponent
import org.dhis2.commons.di.dagger.PerFragment

@PerFragment
@Subcomponent(modules = [JiraModule::class])
interface JiraComponent {
    fun inject(jiraFragment: JiraFragment)
}
