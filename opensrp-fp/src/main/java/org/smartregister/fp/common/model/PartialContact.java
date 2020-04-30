package org.smartregister.fp.common.model;

public class PartialContact extends Contact {

    private String formJsonDraft;
    private Boolean isFinalized;
    private int sortOrder;

    public PartialContact(String baseEntityId, String formType, int contactNo) {
        super(formType, baseEntityId, contactNo);
    }

    public PartialContact() {
        super();
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getFormJsonDraft() {
        return formJsonDraft;
    }

    public void setFormJsonDraft(String formJson) {
        this.formJsonDraft = formJson;
    }

    public Boolean getFinalized() {
        return isFinalized;
    }

    public void setFinalized(Boolean finalized) {
        isFinalized = finalized;
    }

}
