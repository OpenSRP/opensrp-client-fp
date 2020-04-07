package org.smartregister.sample.fp.job;

import com.evernote.android.job.Job;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.job.ExtendedSyncServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.sample.fp.base.BaseUnitTest;

/**
 * Created by ndegwamartin on 07/09/2018.
 */
public class AncJobCreatorTest extends BaseUnitTest {

    private FPJobCreator jobCreator;

    @Before
    public void setUp() {

        jobCreator = new FPJobCreator();
    }

    @Test
    public void testAncJobCreatorInstantiatesCorrectly() {
        Assert.assertNotNull(jobCreator);
    }

    @Test
    public void testAncJobCreatorCreatesCorrectJobForJobTag() {
        //Sync job
        Job job = jobCreator.create(SyncServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof SyncServiceJob);

        //Extended Sync Service Job
        job = jobCreator.create(ExtendedSyncServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof ExtendedSyncServiceJob);

        //Images upload Service Job
        job = jobCreator.create(ImageUploadServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof ImageUploadServiceJob);

        //Pull unique IDs Service Job
        job = jobCreator.create(PullUniqueIdsServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof PullUniqueIdsServiceJob);

        //Validate Sync Service Job
        job = jobCreator.create(ValidateSyncDataServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof ValidateSyncDataServiceJob);

        //View configs Service Job
        job = jobCreator.create(ViewConfigurationsServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof ViewConfigurationsServiceJob);

    }
}
