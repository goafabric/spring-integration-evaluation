package org.goafabric.integration.complex.personanonymizer;

public class PersonItemProcessor {
    public Person process(Person person) {
        person.setFirstName("fake firstName");
        person.setLastName("fake lastName");
        return person;
    }
}
