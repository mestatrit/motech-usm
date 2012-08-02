package org.motechproject.scheduletrackingdemo;

/**
 * Utility class to convert that converts an index number (e.g. 0, 1, etc.) to
 * its corresponding concept name.
 */
public class OpenMrsConceptConverter {

    private static final String[] CONCEPTS = {"65d3d8bd-07d3-4c03-8b80-b5442aead224",
            "50e3b8f9-99c6-4b2c-ab6f-1275870dfaca", "460f6d9a-75d8-4e3c-923a-8c5a9a9c7f68",
            "2d35c7b1-877b-457e-b22f-a0f1bc85bb70" };

    public static String convertToNameFromIndex(int index) {
        if (index < 0 || index > 3) {
            throw new RuntimeException("Concept index must be between 0 and 3");
        }

        return CONCEPTS[index];
    }

    public static int convertToIndex(String conceptName) {
        int index = -1;
        for (int i = 0; i < CONCEPTS.length; i++) {
            if (CONCEPTS[i].equals(conceptName)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public static String getConceptBefore(String conceptName) {
        int index = convertToIndex(conceptName);

        if (index == -1) {
            throw new RuntimeException("Invalid concept name: " + conceptName);
        } else if (index == 0) {
            return conceptName;
        }

        index -= 1;
        return convertToNameFromIndex(index);
    }
}
