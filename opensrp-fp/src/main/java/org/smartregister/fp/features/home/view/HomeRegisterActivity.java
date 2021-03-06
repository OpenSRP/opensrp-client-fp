package org.smartregister.fp.features.home.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.vision.barcode.Barcode;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.FetchStatus;
import org.smartregister.fp.R;
import org.smartregister.fp.common.domain.Contact;
import org.smartregister.fp.common.event.PatientRemovedEvent;
import org.smartregister.fp.common.event.ShowProgressDialogEvent;
import org.smartregister.fp.common.library.FPLibrary;
import org.smartregister.fp.common.util.ConstantsUtils;
import org.smartregister.fp.common.util.DBConstantsUtils;
import org.smartregister.fp.common.util.FPFormUtils;
import org.smartregister.fp.common.util.FPJsonFormUtils;
import org.smartregister.fp.common.util.Utils;
import org.smartregister.fp.features.home.contract.RegisterContract;
import org.smartregister.fp.features.home.presenter.RegisterPresenter;
import org.smartregister.fp.features.home.repository.PatientRepository;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.PERFORM_FORM_TRANSLATION;

public class HomeRegisterActivity extends BaseRegisterActivity implements RegisterContract.View {
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    private AlertDialog recordBirthAlertDialog;
    private boolean isAdvancedSearch = false;
    private boolean isLibrary = false;
    private String advancedSearchQrText = "";
    private HashMap<String, String> advancedSearchFormData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordBirthAlertDialog = createAlertDialog();
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {

            bottomNavigationView.getMenu().findItem(R.id.action_clients).setIcon(R.drawable.ic_icon_nav_clients);
            bottomNavigationView.getMenu().add(Menu.NONE, R.string.action_mec_wheel, Menu.NONE, R.string.action_mec_wheel).setIcon(R.drawable.ic_icon_nav_mec_wheel);

            // remove unused menu
            bottomNavigationView.getMenu().removeItem(R.id.action_search);
            bottomNavigationView.getMenu().removeItem(R.id.action_library);

            BottomNavigationListener bottomNavigationListener = new BottomNavigationListener(this) {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.string.action_mec_wheel) {
                        String pkgName = "com.who.mecwheel";
                        Intent intent = getPackageManager().getLaunchIntentForPackage(pkgName);
                        if (intent == null) {
                            try {
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkgName));
                            } catch (Exception ex) {
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pkgName));
                            }
                        }
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        return false;
                    } else {
                        return super.onNavigationItemSelected(item);
                    }
                }
            };
            bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = findFragmentByPosition(currentPage);
        if (fragment instanceof AdvancedSearchFragment) {
            ((AdvancedSearchFragment) fragment).onBackPressed();
            return;
        } else if (fragment instanceof BaseRegisterFragment) {
            setSelectedBottomBarMenuItem(org.smartregister.R.id.action_clients);
            BaseRegisterFragment registerFragment = (BaseRegisterFragment) fragment;
            if (registerFragment.onBackPressed()) {
                return;
            }
        }
        if (currentPage == 0) {
            super.onBackPressed();
        } else {
            switchToBaseFragment();
            setSelectedBottomBarMenuItem(org.smartregister.R.id.action_clients);
        }
    }

    @Override
    public void initializePresenter() {
        presenter = new RegisterPresenter(this);
    }

    @Override
    public BaseRegisterFragment getRegisterFragment() {
        return new HomeRegisterFragment();
    }

    @Override
    public Fragment[] getOtherFragments() {
        Fragment[] fragments = new Fragment[0];
        return fragments;
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            if (mBaseFragment instanceof HomeRegisterFragment) {
                String locationId = FPLibrary.getInstance().getContext().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                ((RegisterPresenter) presenter).startForm(formName, entityId, metaData, locationId);
            }
        } catch (Exception e) {
            Timber.e(e, "%s --> startFormActivity()", this.getClass().getCanonicalName());
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, JsonWizardFormActivity.class);
        intent.putExtra(ConstantsUtils.JsonFormExtraUtils.JSON, form.toString());
        intent.putExtra(ConstantsUtils.JsonFormExtraUtils.FORM, getFormMetadata());
        intent.putExtra(PERFORM_FORM_TRANSLATION, true);
        startActivityForResult(intent, FPJsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    private Form getFormMetadata() {
        Form form = new Form();
        form.setHomeAsUpIndicator(R.drawable.ic_action_close);
        form.setActionBarBackground(R.color.black);
        form.setSaveLabel(getResources().getString(R.string.save_label));
        return form;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AllConstants.BARCODE.BARCODE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Barcode barcode = data.getParcelableExtra(AllConstants.BARCODE.BARCODE_KEY);
                if (barcode != null) {
                    Timber.d(barcode.displayValue);

                    Fragment fragment = findFragmentByPosition(currentPage);
                    if (fragment instanceof AdvancedSearchFragment) {
                        advancedSearchQrText = barcode.displayValue;
                    } else {
                        mBaseFragment.onQRCodeSucessfullyScanned(barcode.displayValue);
                        mBaseFragment.setSearchTerm(barcode.displayValue);
                    }
                }
            } else {
                Timber.i("NO RESULT FOR QR CODE");
            }
        } /*else {
            onActivityResultExtended(requestCode, resultCode, data);
        }*/
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == FPJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(ConstantsUtils.JsonFormExtraUtils.JSON);
                Timber.d(jsonString);
                if (StringUtils.isNotBlank(jsonString)) {
                    JSONObject form = new JSONObject(jsonString);
                    switch (form.getString(FPJsonFormUtils.ENCOUNTER_TYPE)) {
                        case ConstantsUtils.EventTypeUtils.REGISTRATION:
                            ((RegisterContract.Presenter) presenter).saveRegistrationForm(jsonString, false);
                            break;
                        case ConstantsUtils.EventTypeUtils.CLOSE:
                            ((RegisterContract.Presenter) presenter).closeAncRecord(jsonString);
                            break;
                        case ConstantsUtils.EventTypeUtils.QUICK_CHECK:
                            Contact contact = new Contact();
                            contact.setContactNumber(getIntent().getIntExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, 0));
                            FPFormUtils
                                    .persistPartial(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID), contact);
                            PatientRepository
                                    .updateContactVisitStartDate(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID),
                                            Utils.getDBDateToday());
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Timber.e(e, "%s --> onActivityResultExtended()", this.getClass().getCanonicalName());
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(ConstantsUtils.ConfigurationUtils.HOME_REGISTER);
    }

    @Override
    public void updateInitialsText(String initials) {
        this.userInitials = initials;
    }

    public void switchToBaseFragment() {
        switchToFragment(BaseRegisterActivity.BASE_REG_POSITION);
    }

    public void setSelectedBottomBarMenuItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

    public boolean isLibrary() {
        return isLibrary;
    }

    private void setAdvancedFragmentSearchTerm(String searchTerm) {
        mBaseFragment.setUniqueID(searchTerm);
    }

    private void setFormData(HashMap<String, String> formData) {
        mBaseFragment.setAdvancedSearchFormData(formData);
    }

    public void setLibrary(boolean library) {
        isLibrary = library;
    }

    public boolean isMeItemEnabled() {
        return false;
    }

    public boolean isLibraryItemEnabled() {
        return false;
    }

    public boolean isAdvancedSearchEnabled() {
        return false;
    }

    @NonNull
    protected AlertDialog createAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.record_birth) + "?");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel).toUpperCase(),
                (dialog, which) -> dialog.dismiss());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.record_birth).toUpperCase(),
                (dialog, which) -> FPJsonFormUtils.launchFPCloseForm(HomeRegisterActivity.this));
        return alertDialog;
    }

    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        ((HomeRegisterFragment) mBaseFragment).updateSortAndFilter(filterList, sortField);
        switchToBaseFragment();
    }

    public void startAdvancedSearch() {
        if (isAdvancedSearchEnabled()) {
            try {
                mPager.setCurrentItem(BaseRegisterActivity.ADVANCED_SEARCH_POSITION, false);
            } catch (Exception e) {
                Timber.e(e, "%s --> startAdvancedSearch()", this.getClass().getCanonicalName());
            }
        }

    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showProgressDialogHandler(ShowProgressDialogEvent showProgressDialogEvent) {
        if (showProgressDialogEvent != null) {
            showProgressDialog(R.string.saving_dialog_title);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void removePatientHandler(PatientRemovedEvent event) {
        if (event != null) {
            Utils.removeStickyEvent(event);
            refreshList(FetchStatus.fetched);
            hideProgressDialog();
        }
    }

    @Override
    public void startRegistration() {
        startFormActivity(ConstantsUtils.JsonFormUtils.FP_REGISTER, null, null);
    }

    public void showRecordBirthPopUp(CommonPersonObjectClient client) {
        //This is required
        getIntent()
                .putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, client.getColumnmaps().get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID));

        recordBirthAlertDialog.setMessage(String.format(this.getString(R.string.record_birth_popup_message),
                Utils.getGestationAgeFromEDDate(client.getColumnmaps().get(DBConstantsUtils.KeyUtils.EDD)),
                Utils.convertDateFormat(Utils.dobStringToDate(client.getColumnmaps().get(DBConstantsUtils.KeyUtils.EDD)),
                        dateFormatter), Utils.getDuration(client.getColumnmaps().get(DBConstantsUtils.KeyUtils.EDD)),
                client.getColumnmaps().get(DBConstantsUtils.KeyUtils.FIRST_NAME)));
        recordBirthAlertDialog.show();
    }

    public void setAdvancedSearch(boolean advancedSearch) {
        isAdvancedSearch = advancedSearch;
    }

    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
        this.advancedSearchFormData = advancedSearchFormData;
    }
}
