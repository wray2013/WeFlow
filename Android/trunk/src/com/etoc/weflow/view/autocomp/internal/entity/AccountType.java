package com.etoc.weflow.view.autocomp.internal.entity;


import com.etoc.weflow.R;
import com.etoc.weflow.view.autocomp.internal.filter.AccountFilter;
import com.etoc.weflow.view.autocomp.internal.filter.AccountFilterImpl;

import android.content.res.TypedArray;


/**
 * @author keishin.yokomaku
 * @since 2014/03/05
 */
public enum AccountType {
    ANY(0, new AccountFilterImpl(".*")),
    EMAIL(1, new AccountFilterImpl("[a-zA-Z0-9._%+-][a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}")),
    PHONE_NUMBER(2, new AccountFilterImpl("[0-9]{11,12}")),
    QQ_NUMBER(3,new AccountFilterImpl("[1-9][0-9]{5,10}"));

    private final int mTypeId;
    private final AccountFilter mFilter;

    private AccountType(final int typeId, final AccountFilter filter) {
        mTypeId = typeId;
        mFilter = filter;
    }

    public static AccountType valueOf(TypedArray array) {
        int typeId = array.getInt(R.styleable.AccountAutoCompleteEditText_accountType, 0);
        for (AccountType type : values()) {
            if (type.getTypeId() == typeId) {
                return type;
            }
        }
        return ANY;
    }

    public int getTypeId() {
        return mTypeId;
    }

    public AccountFilter getFilter() {
        return mFilter;
    }
}