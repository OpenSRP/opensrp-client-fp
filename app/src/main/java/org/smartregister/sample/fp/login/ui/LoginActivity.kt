package org.smartregister.sample.fp.login.ui

import android.widget.Toast
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.smartregister.sample.fp.R
import org.smartregister.sample.fp.event.ViewConfigurationSyncCompleteEvent
import org.smartregister.sample.fp.login.presenter.LoginPresenter
import org.smartregister.view.activity.BaseLoginActivity
import org.smartregister.view.contract.BaseLoginContract
import timber.log.Timber

class LoginActivity : BaseLoginActivity(), BaseLoginContract.View {
    override fun onResume() {
        super.onResume()
        mLoginPresenter.processViewCustomizations()
        if (!mLoginPresenter.isUserLoggedOut) {
            goToHome(false)
        }
    }

    override fun goToHome(remote: Boolean) {
        // go to main page after success full login
        gotToHomeRegister(remote)
        if (remote)
            Toast.makeText(this, "Remote Login Success", Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this, "Local Login Success", Toast.LENGTH_LONG).show()
    }

    private fun gotToHomeRegister(remote: Boolean) {
        /*val intent = Intent(this, BaseHomeRegisterActivity::class.java)
        intent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, remote)
        startActivity(intent)*/
    }

    override fun getContentView(): Int {
        return R.layout.activity_login
    }

    override fun initializePresenter() {
        mLoginPresenter = LoginPresenter(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun refreshViews(syncCompleteEvent: ViewConfigurationSyncCompleteEvent?) {
        if (syncCompleteEvent != null) {
            Timber.d("Refreshing Login View...")
            mLoginPresenter.processViewCustomizations()
        }
    }
}