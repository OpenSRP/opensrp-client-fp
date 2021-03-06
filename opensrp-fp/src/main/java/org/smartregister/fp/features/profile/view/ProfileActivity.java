package org.smartregister.fp.features.profile.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.fp.R;
import org.smartregister.fp.common.event.ClientDetailsFetchedEvent;
import org.smartregister.fp.common.event.PatientRemovedEvent;
import org.smartregister.fp.common.library.FPLibrary;
import org.smartregister.fp.common.util.ConstantsUtils;
import org.smartregister.fp.common.util.DBConstantsUtils;
import org.smartregister.fp.common.util.FPJsonFormUtils;
import org.smartregister.fp.common.util.Utils;
import org.smartregister.fp.common.view.CopyToClipboardDialog;
import org.smartregister.fp.features.home.repository.PatientRepository;
import org.smartregister.fp.features.profile.adapter.ProfileViewPagerAdapter;
import org.smartregister.fp.features.profile.contract.ProfileContract;
import org.smartregister.fp.features.profile.presenter.ProfilePresenter;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.HashMap;

import timber.log.Timber;

import static org.smartregister.fp.common.util.ConstantsUtils.DateFormatPatternUtils.DD_MM_YYYY;
import static org.smartregister.fp.common.util.ConstantsUtils.DateFormatPatternUtils.FOLLOWUP_VISIT_BUTTON_FORMAT;
import static org.smartregister.fp.common.util.ConstantsUtils.DateFormatPatternUtils.YYYY_MM_DD;

public class ProfileActivity extends BaseProfileActivity implements ProfileContract.View {
    private TextView nameView;
    private TextView ageView;
    private TextView genderView;
    private TextView clientIdView;
    private ImageView imageView;
    private String phoneNumber;
    private HashMap<String, String> detailMap;
    private Button dueButton;
    private TextView taskTabCount;
    private String contactNo;
    private TextView btnStartFPVisit;

    @Override
    protected void onCreation() {
        super.onCreation();
        CollapsingToolbarLayout collapsingToolbarLayout = appBarLayout.findViewById(org.smartregister.R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(false);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        ((ProfilePresenter) presenter).refreshProfileView(baseEntityId);
        registerEventBus();
        getTasksCount(baseEntityId);
        updateBtnStartFPVisit(baseEntityId);
    }

    protected void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    private void getTasksCount(String baseEntityId) {
        if (StringUtils.isNotBlank(baseEntityId) && StringUtils.isNotBlank(contactNo)) {
            ((ProfilePresenter) presenter).getTaskCount(baseEntityId);
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = new ProfilePresenter(this);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        getButtonAlertStatus();
        genderView = findViewById(R.id.textview_detail_two);
        ageView = findViewById(R.id.textview_detail_three);
        clientIdView = findViewById(R.id.textview_detail_one);
        nameView = findViewById(R.id.textview_name);
        imageView = findViewById(R.id.imageview_profile);
        dueButton = findViewById(R.id.profile_overview_due_button);
        btnStartFPVisit = findViewById(R.id.btn_start_visit);
        updateTasksTabTitle();
    }

    private void updateBtnStartFPVisit(String baseEntityId) {
        String todayDate = Utils.getTodaysDate();
        HashMap<String, String> clientProfileDetails = PatientRepository.getClientProfileDetails(baseEntityId);
        if (clientProfileDetails != null) {
            String nextContactDate = clientProfileDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE);
            int compareTwoDatesResult = Utils.compareTwoDates(Utils.formatDateToPattern(todayDate, YYYY_MM_DD, DD_MM_YYYY), Utils.formatDateToPattern(nextContactDate, YYYY_MM_DD, DD_MM_YYYY));
            String formattedNextContactDate = Utils.formatDateToPattern(nextContactDate, YYYY_MM_DD, FOLLOWUP_VISIT_BUTTON_FORMAT);
            Utils.updateBtnStartVisit(compareTwoDatesResult, btnStartFPVisit, formattedNextContactDate, this, detailMap);
        }
    }

    private void getButtonAlertStatus() {
        detailMap = (HashMap<String, String>) getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
        contactNo = String.valueOf(Utils.getTodayContact(detailMap.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
    }

    protected void updateTasksTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.tasks_tab_title, null);
        TextView taskTabTitle = taskTabTitleLayout.findViewById(R.id.tasks_title);
        taskTabTitle.setText(this.getString(R.string.tasks));
        taskTabCount = taskTabTitleLayout.findViewById(R.id.tasks_count);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getSupportFragmentManager());

        ProfileOverviewFragment profileOverviewFragment = ProfileOverviewFragment.newInstance(this.getIntent().getExtras());
        ClientHistoryFragment clientHistoryFragment = ClientHistoryFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileOverviewFragment, this.getString(R.string.overview));
        adapter.addFragment(clientHistoryFragment, this.getString(R.string.history));

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1 && clientHistoryFragment.hasRecords()) {
                    getBtnStartFPVisit().setVisibility(View.GONE);
                } else {
                    getBtnStartFPVisit().setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return viewPager;
    }

    @Override
    protected void fetchProfileData() {
        String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        ((ProfilePresenter) presenter).fetchProfileData(baseEntityId);
    }

    @Override
    public void onBackPressed() {
        Utils.navigateToHomeRegister(this, false, FPLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchPhoneDialer(phoneNumber);
            } else {
                displayToast(R.string.allow_phone_call_management);
            }
        }
    }

    protected void launchPhoneDialer(String phoneNumber) {
        if (isPermissionGranted()) {
            try {
                Intent intent = getTelephoneIntent(phoneNumber);
                startActivity(intent);
            } catch (Exception e) {
                Timber.i("No dial application so we launch copy to clipboard...");
                CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(this, R.style.copy_clipboard_dialog);
                copyToClipboardDialog.setContent(phoneNumber);
                copyToClipboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                copyToClipboardDialog.show();
            }
        }
    }

    protected boolean isPermissionGranted() {
        return PermissionUtils.isPermissionGranted(this, Manifest.permission.READ_PHONE_STATE,
                PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);
    }

    @NonNull
    protected Intent getTelephoneIntent(String phoneNumber) {
        return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        // When user click home menu item then quit this activity.
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            Utils.navigateToHomeRegister(this, false, FPLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
        } else if (itemId == R.id.menu_btn_call) {
            launchPhoneDialer(phoneNumber);
        } else if (itemId == R.id.menu_btn_start_visit) {
            Utils.continueToContact(detailMap, this);
        } else if (itemId == R.id.menu_btn_close_fp_record) {
            FPJsonFormUtils.launchFPCloseForm(ProfileActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_activity, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AllSharedPreferences allSharedPreferences = FPLibrary.getInstance().getContext().allSharedPreferences();
        if (requestCode == FPJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            ((ProfilePresenter) presenter).processFormDetailsSave(data, allSharedPreferences);
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startFormForEdit(ClientDetailsFetchedEvent event) {
        if (event != null && event.isEditMode()) {
            String formMetadata = FPJsonFormUtils.getAutoPopulatedJsonEditRegisterFormString(this, event.getRegisteredClient());
            try {
                FPJsonFormUtils.startFormForEdit(this, FPJsonFormUtils.REQUEST_CODE_GET_JSON, formMetadata);
            } catch (Exception e) {
                Timber.e(e, " --> startFormForEdit");
            }
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void refreshProfileTopSection(ClientDetailsFetchedEvent event) {
        if (event != null && !event.isEditMode()) {
            Utils.removeStickyEvent(event);
            ((ProfilePresenter) presenter).refreshProfileTopSection(event.getRegisteredClient());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void removePatient(PatientRemovedEvent event) {
        if (event != null) {
            Utils.removeStickyEvent(event);
            hideProgressDialog();
            finish();
        }
    }

    @Override
    public void setProfileName(String fullName) {
        this.patientName = fullName;
        nameView.setText(fullName);
    }

    @Override
    public void setProfileID(String clientId) {
        clientIdView.setText("ID: " + clientId);
    }

    @Override
    public void setProfileAge(String age) {
        ageView.setText("Age: " + age);

    }

    @Override
    public void setProfileGender(String gender) {
        genderView.setText(StringUtils.capitalize(gender));
    }

    @Override
    public void setProfileImage(String baseEntityId) {
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, Utils.getProfileImageResourceIdentifier());
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void setTaskCount(String taskCount) {
        getTaskTabCount().setText(taskCount);
    }

    public TextView getTaskTabCount() {
        return taskTabCount;
    }

    public Button getDueButton() {
        return dueButton;
    }

    public ProfilePresenter getProfilePresenter() {
        return (ProfilePresenter) presenter;
    }

    @NonNull
    public TextView getBtnStartFPVisit() {
        return btnStartFPVisit;
    }
}

