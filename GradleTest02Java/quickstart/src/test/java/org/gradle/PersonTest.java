package org.gradle;

import org.junit.Test;
import static org.junit.Assert.*;

public class PersonTest {
    @Test // signify the method to be tested
    public void canConstructAPersonWithAName() {
        Person person = new Person("Larry");
        assertEquals("Larry", person.getName());
    }
}
