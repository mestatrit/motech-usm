package org.motechproject.mapper.service;

public class NonNullPersonFieldUpdateStrategy  implements PersonFieldUpdateStrategy {

    @Override
    public boolean canUpdateField(String name, Object value) {
        return value != null;
    }
}
