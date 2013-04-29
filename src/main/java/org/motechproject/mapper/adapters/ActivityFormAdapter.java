package org.motechproject.mapper.adapters;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.mapper.domain.MRSActivity;

/**
 * Adapts a particular form by an activity type, such as encounters, registrations, drugs orders, etc.
 *
 */
public interface ActivityFormAdapter {

    void adaptForm(CommcareForm form, MRSActivity activity);

}
