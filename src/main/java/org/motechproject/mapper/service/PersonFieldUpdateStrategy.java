package org.motechproject.mapper.service;

public interface PersonFieldUpdateStrategy {
    public boolean canUpdateField(String name, Object value);

}
