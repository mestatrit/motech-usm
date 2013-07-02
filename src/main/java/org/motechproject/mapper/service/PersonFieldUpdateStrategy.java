package org.motechproject.mapper.service;

public interface PersonFieldUpdateStrategy {
    public boolean canUpdate(String fieldName, Object fieldValue);

    public void markUpdated(String fieldName);

}
