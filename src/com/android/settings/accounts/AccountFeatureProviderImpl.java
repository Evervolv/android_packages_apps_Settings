package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;

import com.android.settings.R;

public class AccountFeatureProviderImpl implements AccountFeatureProvider {

    private String mAccountType;

    public AccountFeatureProviderImpl(Context context) {
        mAccountType = context.getResources().getString(R.string.config_default_account_type);
    }

    @Override
    public String getAccountType() {
        return TextUtils.isEmpty(mAccountType) ? null : mAccountType;
    }

    @Override
    public Account[] getAccounts(Context context) {
        return TextUtils.isEmpty(mAccountType) ? new Account[0]
                : AccountManager.get(context).getAccountsByType(mAccountType);
    }
}
