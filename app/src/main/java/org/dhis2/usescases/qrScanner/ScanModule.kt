package org.dhis2.usescases.qrScanner

import dagger.Module
import dagger.Provides
import org.dhis2.commons.di.dagger.PerActivity
import org.hisp.dhis.android.core.D2

@Module
class ScanModule(private val optionSetUid: String?) {

    @Provides
    @PerActivity
    internal fun providesRepository(d2: D2): ScanRepository {
        return ScanRepository(d2, optionSetUid)
    }
}
