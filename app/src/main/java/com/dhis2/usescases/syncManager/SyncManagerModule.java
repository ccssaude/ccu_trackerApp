package com.dhis2.usescases.syncManager;

import android.content.Context;

import com.dhis2.data.dagger.PerFragment;
import com.dhis2.data.metadata.MetadataRepository;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import dagger.Module;
import dagger.Provides;

/**
 * Created by frodriguez on 4/13/2018.
 */

@Module
public final class SyncManagerModule {

    private Context context;

    public SyncManagerModule(Context context) {
        this.context = context;
    }

    @Provides
    @PerFragment
    SyncManagerContracts.Presenter providePresenter(MetadataRepository metadataRepository){
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        return new SyncManagerPresenter(metadataRepository, firebaseJobDispatcher);
    }
}
