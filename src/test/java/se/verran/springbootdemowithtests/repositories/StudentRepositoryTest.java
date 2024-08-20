package se.verran.springbootdemowithtests.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import se.verran.springbootdemowithtests.entities.Student;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        // Skapa en student med alla obligatoriska fält ifyllda
        Student student = new Student();
        student.setFirstName("Rickard");
        student.setLastName("Ekstedt");
        student.setEmail("test@example.com");
        student.setBirthDate(LocalDate.of(1981, 3, 2));
        studentRepository.save(student);
    }


    @Test
    void existsStudentByEmailShouldReturnTrueWhenEmailExists() {
        // När vi söker efter en e-post som finns i databasen
        boolean exists = studentRepository.existsStudentByEmail("test@example.com");

        // Verifiera att metoden returnerar true
        assertTrue(exists, "Student with the email should exist");
    }

    @Test
    void existsStudentByEmailShouldReturnFalseWhenEmailDoesNotExist() {
        // När vi söker efter en e-post som inte finns i databasen
        boolean exists = studentRepository.existsStudentByEmail("nonexistent@example.com");

        // Verifiera att metoden returnerar false
        assertFalse(exists, "Student with the email should not exist");
    }
}
