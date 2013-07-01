package org.motechproject.mapper.service;

import org.springframework.stereotype.Component;

@Component
public class PersonFieldUpdateStrategyFactory {

    public PersonFieldUpdateStrategy getStrategyForUpdate() {
        return new NonNullPersonFieldUpdateStrategy();
    }

    public PersonFieldUpdateStrategy getStrategyForCreate() {
        return new NonNullPersonFieldUpdateStrategy();
    }
}
