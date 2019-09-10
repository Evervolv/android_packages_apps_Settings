package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;

public class AccountFeatureProviderImpl implements AccountFeatureProvider {
    @Override
    public String getAccountType() {
        final String accountType = FeatureFactory.getAppContext().getString(R.string.account_type);
        if (!accountType.isEmpty()) {
            return accountType;
        }
        return null;
    }

    @Override
    public Account[] getAccounts(Context context) {
        final String type = getAccountType();
        if (type != null) {
            return AccountManager.get(context).getAccountsByType(getAccountType());
        }
        return new Account[0];
    }
}
