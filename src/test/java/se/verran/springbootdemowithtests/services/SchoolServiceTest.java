package se.verran.springbootdemowithtests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import se.verran.springbootdemowithtests.entities.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SchoolServiceTest {
    SchoolService schoolService;
    private StudentService mockedStudentService;

    @BeforeEach
    void setUp() {
        // Mocka beroendet
        mockedStudentService = mock(StudentService.class);

        // Injektera mocken i StudentService
        schoolService = new SchoolService(mockedStudentService);
    }
    // Hjälpfunktion för student med betyg
    private Student createStudentWithGrade(Double grade) {
        Student student = new Student();
        student.setJavaProgrammingGrade(grade);
        return student;
    }

    @Test
    void shouldReturnErrorWhenNumberOfGroupsIsLessThanTwo() {
        // Given
        List<Student> students = List.of(new Student(), new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int numberOfGroups = 1;

        // When
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        // Then
        assertThat(result).isEqualTo("There should be at least two groups");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnErrorWhenNumberOfGroupsIsGreaterThanNumberOfStudents() {
        // Given
        List<Student> students = List.of(new Student(), new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int numberOfGroups = 4;

        // When
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        // Then
        assertThat(result).isEqualTo("Not able to divide 3 students into 4 groups");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnErrorWhenStudentsPerGroupIsLessThanTwo() {
        // Given
        List<Student> students = List.of(new Student(), new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int numberOfGroups = 3;

        // When
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        // Then
        assertThat(result).isEqualTo("Not able to manage 3 groups with 3 students");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnCorrectDivisionWhenStudentsCanBeEquallyDivided() {
        // Given
        List<Student> students = List.of(new Student(), new Student(), new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int numberOfGroups = 2;

        // When
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        // Then
        assertThat(result).isEqualTo("2 groups could be formed with 2 students per group");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnCorrectDivisionWithRemainderWhenStudentsCannotBeEquallyDivided() {
        // Given
        List<Student> students = List.of(new Student(), new Student(), new Student(), new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int numberOfGroups = 2;

        // When
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        // Then
        assertThat(result).isEqualTo("2 groups could be formed with 2 students per group, but that would leave 1 student hanging");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnErrorWhenSizeOfGroupIsLessThanTwo() {
        // Given
        List<Student> students = List.of(new Student(), new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int studentsPerGroup = 1;

        // When
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        // Then
        assertThat(result).isEqualTo("Size of group should be at least 2");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnErrorWhenNotEnoughStudentsToFormGroups() {
        // Given
        List<Student> students = List.of(new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int studentsPerGroup = 3;

        // When
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        // Then
        assertThat(result).isEqualTo("Not able to manage groups of 3 with only 2 students");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnCorrectNumberOfGroupsWithoutRemainder() {
        // Given
        List<Student> students = List.of(new Student(), new Student(), new Student(), new Student(), new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int studentsPerGroup = 3;

        // When
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        // Then
        assertThat(result).isEqualTo("3 students per group is possible, there will be 2 groups");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnCorrectNumberOfGroupsWithRemainder() {
        // Given
        List<Student> students = List.of(new Student(), new Student(), new Student(), new Student(), new Student());
        when(mockedStudentService.getAllStudents()).thenReturn(students);
        int studentsPerGroup = 2;

        // When
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        // Then
        assertThat(result).isEqualTo("2 students per group is possible, there will be 2 groups, there will be 1 student hanging");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldThrowExceptionWhenNoStudentsFoundForAverageGrade() {
        // Given
        when(mockedStudentService.getAllStudents()).thenReturn(List.of());

        // When & Then
        ResponseStatusException thrownException = assertThrows(ResponseStatusException.class, () -> {
            schoolService.calculateAverageGrade();
        });

        // Kontrollera felmeddelandet
        assertThat(thrownException.getMessage()).contains("404 NOT_FOUND \"No students found\"");
    }



    @Test
    void shouldCalculateAverageGradeCorrectly() {
        // Given
        List<Student> students = List.of(
                createStudentWithGrade(70.0),
                createStudentWithGrade(80.0),
                createStudentWithGrade(90.0)
        );
        when(mockedStudentService.getAllStudents()).thenReturn(students);

        // When
        String result = schoolService.calculateAverageGrade();

        // Then
        assertThat(result).isEqualTo("Average grade is 80,0");
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldCalculateAverageGradeWhenAllGradesAreTheSame() {
        // Given
        List<Student> students = List.of(
                createStudentWithGrade(85.0),
                createStudentWithGrade(85.0),
                createStudentWithGrade(85.0)
        );
        when(mockedStudentService.getAllStudents()).thenReturn(students);

        // When
        String result = schoolService.calculateAverageGrade();

        // Then
        assertThat(result).isEqualTo("Average grade is 85,0");
        verify(mockedStudentService, times(1)).getAllStudents();
    }


    @Test
    void shouldCalculateAverageGradeWithDecimalValues() {
        // Given
        List<Student> students = List.of(
                createStudentWithGrade(75.5),
                createStudentWithGrade(85.5)
        );
        when(mockedStudentService.getAllStudents()).thenReturn(students);

        // When
        String result = schoolService.calculateAverageGrade();

        // Then
        assertThat(result).isEqualTo("Average grade is 80,5");
        verify(mockedStudentService, times(1)).getAllStudents();
    }


    @Test
    void shouldThrowExceptionWhenNoStudentsFoundForTopScoringStudents() {
        // Given
        when(mockedStudentService.getAllStudents()).thenReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> schoolService.getTopScoringStudents())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"No students found\""); // Kontrollera meddelandet direkt

        verify(mockedStudentService, times(1)).getAllStudents();
    }



    @Test
    void shouldReturnTopScoringStudentsWhenMultipleStudentsExist() {
        // Given
        List<Student> students = List.of(
                createStudentWithGrade(90.0),
                createStudentWithGrade(85.0),
                createStudentWithGrade(80.0),
                createStudentWithGrade(75.0)
        );
        when(mockedStudentService.getAllStudents()).thenReturn(students);

        // When
        List<Student> topStudents = schoolService.getTopScoringStudents();

        // Then
        assertThat(topStudents).hasSize(1); // 20% of 4 students is 0.8, which rounds up to 1 student
        assertThat(topStudents.get(0).getJavaProgrammingGrade()).isEqualTo(90.0);
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnTopScoringStudentsWhenOnlyFewStudentsExist() {
        // Given
        List<Student> students = List.of(
                createStudentWithGrade(88.0),
                createStudentWithGrade(92.0)
        );
        when(mockedStudentService.getAllStudents()).thenReturn(students);

        // When
        List<Student> topStudents = schoolService.getTopScoringStudents();

        // Then
        assertThat(topStudents).hasSize(1); // 20% of 2 students is 0.4, which rounds up to 1 student
        assertThat(topStudents.get(0).getJavaProgrammingGrade()).isEqualTo(92.0);
        verify(mockedStudentService, times(1)).getAllStudents();
    }

    @Test
    void shouldReturnTopScoringStudentsWithSameGrades() {
        // Given
        List<Student> students = List.of(
                createStudentWithGrade(85.0),
                createStudentWithGrade(85.0),
                createStudentWithGrade(85.0),
                createStudentWithGrade(85.0)
        );
        when(mockedStudentService.getAllStudents()).thenReturn(students);

        // When
        List<Student> topStudents = schoolService.getTopScoringStudents();

        // Then
        assertThat(topStudents).hasSize(1); // 20% of 4 students is 0.8, which rounds up to 1 student
        assertThat(topStudents.get(0).getJavaProgrammingGrade()).isEqualTo(85.0);
        verify(mockedStudentService, times(1)).getAllStudents();
    }
}
