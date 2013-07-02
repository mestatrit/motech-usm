package org.motechproject.mapper.service;

public class NonNullPersonFieldUpdateStrategy implements PersonFieldUpdateStrategy {

    public NonNullPersonFieldUpdateStrategy() {
    }

    @Override
    public boolean canUpdate(String fieldName, Object fieldValue) {
        return fieldValue != null;
    }

    @Override
    public void markUpdated(String fieldName) {

    }

}
