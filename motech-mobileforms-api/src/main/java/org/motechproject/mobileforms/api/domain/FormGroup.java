package org.motechproject.mobileforms.api.domain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

/** Represents set of form definitions provided in json config file. */
@TypeDiscriminator("doc.type === 'FormGroup'")
public class FormGroup extends MotechBaseDataObject {
    @JsonProperty
    private String name;
    
    @JsonProperty
    Integer index;
    
    @JsonProperty
    private List<Form> forms;

    public FormGroup() {
    }
    
    public FormGroup(String name, List<Form> forms) {
        this.name = name;
        this.forms = forms;
    }

    public String getName() {
        return name;
    }

    public List<Form> getForms() {
        return forms;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormGroup)) {
            return false;
        }
        FormGroup formGroup = (FormGroup) o;
        if (forms != null ? !forms.equals(formGroup.forms) : formGroup.forms != null) {
            return false;
        }

        if (name != null ? !name.equals(formGroup.name) : formGroup.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (forms != null ? forms.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FormGroup{" +
                "name='" + name + '\'' +
                ", forms=" + forms +
                '}';
    }
}
