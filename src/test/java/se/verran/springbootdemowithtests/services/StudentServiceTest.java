package se.verran.springbootdemowithtests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import se.verran.springbootdemowithtests.entities.Student;
import se.verran.springbootdemowithtests.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    private StudentService studentService;
    private StudentRepository mockedStudentRepository;

    @BeforeEach
    void setUp() {
        // Mocka beroendet
        mockedStudentRepository = mock(StudentRepository.class);

        // Injektera mocken i StudentService
        studentService = new StudentService(mockedStudentRepository);
    }

    @Test
    void addStudentShouldThrowExceptionIfEmailExists() {
        // Given
        Student student = new Student();
        student.setEmail("test@example.com");
        when(mockedStudentRepository.existsStudentByEmail(student.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> studentService.addStudent(student))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email test@example.com already exists");

        verify(mockedStudentRepository, never()).save(student);
    }

    @Test
    void addStudentShouldSaveStudentIfEmailDoesNotExist() {
        // Given
        Student student = new Student();
        student.setEmail("test@example.com");
        when(mockedStudentRepository.existsStudentByEmail(student.getEmail())).thenReturn(false);
        when(mockedStudentRepository.save(student)).thenReturn(student);

        // When
        Student savedStudent = studentService.addStudent(student);

        // Then
        assertThat(savedStudent).isEqualTo(student);
        verify(mockedStudentRepository, times(1)).save(student);
    }

    @Test
    void getAllStudentsShouldReturnAllStudents() {
        // Given
        List<Student> students = List.of(new Student(), new Student());
        when(mockedStudentRepository.findAll()).thenReturn(students);

        // When
        List<Student> result = studentService.getAllStudents();

        // Then
        assertThat(result).isEqualTo(students);
        verify(mockedStudentRepository, times(1)).findAll();
    }

    @Test
    void deleteStudentShouldThrowExceptionIfStudentNotFound() {
        // Given
        int studentId = 1;
        when(mockedStudentRepository.existsById(studentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> studentService.deleteStudent(studentId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find and delete student by id " + studentId);

        verify(mockedStudentRepository, never()).deleteById(studentId);
    }

    @Test
    void deleteStudentShouldDeleteStudentIfExists() {
        // Given
        int studentId = 1;
        when(mockedStudentRepository.existsById(studentId)).thenReturn(true);

        // When
        studentService.deleteStudent(studentId);

        // Then
        verify(mockedStudentRepository, times(1)).deleteById(studentId);
    }

    @Test
    void getStudentByIdShouldReturnStudentIfExists() {
        // Given
        int studentId = 1;
        Student student = new Student();
        when(mockedStudentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // When
        Student result = studentService.getStudentById(studentId);

        // Then
        assertThat(result).isEqualTo(student);
        verify(mockedStudentRepository, times(1)).findById(studentId);
    }

    @Test
    void getStudentByIdShouldThrowExceptionIfStudentNotFound() {
        // Given
        int studentId = 1;
        when(mockedStudentRepository.findById(studentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.getStudentById(studentId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find student by id " + studentId);

        verify(mockedStudentRepository, times(1)).findById(studentId);
    }

    @Test
    void updateStudentShouldThrowExceptionIfStudentNotFound() {
        // Given
        Student student = new Student();
        student.setId(1);
        when(mockedStudentRepository.existsById(student.getId())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> studentService.updateStudent(student))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find and update student by id " + student.getId());

        verify(mockedStudentRepository, never()).save(student);
    }

    @Test
    void updateStudentShouldSaveAndReturnUpdatedStudentIfExists() {
        // Given
        Student student = new Student();
        student.setId(1);
        when(mockedStudentRepository.existsById(student.getId())).thenReturn(true);
        when(mockedStudentRepository.save(student)).thenReturn(student);

        // When
        Student updatedStudent = studentService.updateStudent(student);

        // Then
        assertThat(updatedStudent).isEqualTo(student);
        verify(mockedStudentRepository, times(1)).save(student);
    }
    @Test
    void setGradeForStudentByIdShouldThrowExceptionForInvalidGradeFormat() {
        // Given
        int studentId = 1;
        String invalidGrade = "ABC";

        // When & Then
        assertThatThrownBy(() -> studentService.setGradeForStudentById(studentId, invalidGrade))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Valid grades are 0.0 - 5.0");
    }

    @Test
    void setGradeForStudentByIdShouldThrowExceptionForGradeOutOfRange() {
        // Given
        int studentId = 1;
        String invalidGradeHigh = "5.5";
        String invalidGradeLow = "-1";

        // When & Then
        assertThatThrownBy(() -> studentService.setGradeForStudentById(studentId, invalidGradeHigh))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Valid grades are 0.0 - 5.0");

        assertThatThrownBy(() -> studentService.setGradeForStudentById(studentId, invalidGradeLow))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Valid grades are 0.0 - 5.0");
    }

    @Test
    void setGradeForStudentByIdShouldThrowExceptionIfStudentNotFound() {
        // Given
        int studentId = 1;
        String validGrade = "4.0";
        when(mockedStudentRepository.findById(studentId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.setGradeForStudentById(studentId, validGrade))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find and update grades for student by id " + studentId);
    }

    @Test
    void setGradeForStudentByIdShouldSetGradeIfStudentExists() {
        // Given
        int studentId = 1;
        String validGrade = "4.0";
        Student student = new Student();
        student.setId(studentId);
        when(mockedStudentRepository.findById(studentId)).thenReturn(java.util.Optional.of(student));
        when(mockedStudentRepository.save(any(Student.class))).thenReturn(student);

        // When
        Student updatedStudent = studentService.setGradeForStudentById(studentId, validGrade);

        // Then
        assertThat(updatedStudent.getJavaProgrammingGrade()).isEqualTo(4.0);
        verify(mockedStudentRepository, times(1)).save(student);
    }
}
