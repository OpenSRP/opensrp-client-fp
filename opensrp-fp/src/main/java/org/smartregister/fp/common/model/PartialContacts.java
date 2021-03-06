package org.smartregister.fp.common.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.fp.common.library.FPLibrary;
import org.smartregister.fp.common.util.ConstantsUtils;
import org.smartregister.fp.common.util.DBConstantsUtils;
import org.smartregister.fp.features.home.repository.PartialContactRepository;

import java.util.List;
import java.util.Map;

public class PartialContacts {
    private Map<String, String> details;
    private String referral;
    private String baseEntityId;
    private boolean isFirst;
    private PartialContactRepository partialContactRepository;
    private List<PartialContact> partialContactList;

    public PartialContacts(Map<String, String> details, String referral, String baseEntityId, boolean isFirst) {
        this.details = details;
        this.referral = referral;
        this.baseEntityId = baseEntityId;
        this.isFirst = isFirst;
    }

    public PartialContactRepository getPartialContactRepository() {
        return partialContactRepository;
    }

    public List<PartialContact> getPartialContactList() {
        return partialContactList;
    }

    public PartialContacts invoke() {
        partialContactRepository = FPLibrary.getInstance().getPartialContactRepository();

        if (partialContactRepository != null) {
            if (isFirst) {
                partialContactList = partialContactRepository.getPartialContacts(baseEntityId, 1);
            } else {
                if (referral != null) {
                    partialContactList = partialContactRepository
                            .getPartialContacts(baseEntityId, getContactFromReferral(details.get(ConstantsUtils.REFERRAL)));
                } else {
                    partialContactList = partialContactRepository.getPartialContacts(baseEntityId,
                            Integer.valueOf(details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
                }
            }
        } else {
            partialContactList = null;
        }
        return this;
    }

    private int getContactFromReferral(String referral) {
        int contactNo = 0;
        if (StringUtils.isNotBlank(referral) && referral.contains("-")) {
            String[] referralSplit = referral.split("-");
            if (referralSplit.length == 2) {
                contactNo = Integer.parseInt(referralSplit[1]);
            }
        }
        return contactNo;
    }
}

