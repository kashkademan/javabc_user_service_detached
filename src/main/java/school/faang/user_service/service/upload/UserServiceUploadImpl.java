package school.faang.user_service.service.upload;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.csv.CsvUploadResponseDto;
import school.faang.user_service.dto.csv.StudentCsvDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.PasswordGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceUploadImpl implements UserServiceUpload {
    private final CsvMapper csvMapper;
    private final UserMapper csvUserMapper;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final EducationRepository educationRepository;

    @Override
    @Transactional
    public CsvUploadResponseDto processStudentsCsv(MultipartFile file) {
        List<StudentCsvDto> students = readCsvFile(file);
        Map<String, Country> countriesMap = getAllCountriesMap();

        List<String> validationErrors = validateAllStudents(students);

        if (!validationErrors.isEmpty()) {
            log.error("Validation failed for CSV file. Found {} errors", validationErrors.size());
            throw new DataValidationException("Validation failed for CSV file. Errors: " + String.join("; ", validationErrors));
        }

        List<User> processedUsers = processAllStudents(students, countriesMap);

        log.info("Successfully processed {} students from CSV file", processedUsers.size());
        return buildSuccessResponse(students.size(), processedUsers.size());
    }

    private List<StudentCsvDto> readCsvFile(MultipartFile file) {
        CsvSchema schema = csvMapper.schemaFor(StudentCsvDto.class).withHeader();

        try (InputStream inputStream = file.getInputStream()) {
            MappingIterator<StudentCsvDto> iterator = csvMapper
                    .readerFor(StudentCsvDto.class)
                    .with(schema)
                    .readValues(inputStream);
            return iterator.readAll();
        } catch (IOException e) {
            throw new DataValidationException("Error reading CSV file: " + e.getMessage());
        }
    }

    private List<String> validateAllStudents(List<StudentCsvDto> students) {
        List<String> errors = new ArrayList<>();

        for (StudentCsvDto studentDto : students) {
            try {
                validateStudent(studentDto);
            } catch (Exception e) {
                String studentIdentifier = getStudentIdentifier(studentDto);
                errors.add("Error validating student " + studentIdentifier + ": " + e.getMessage());
                log.error("Error validating student {}", studentIdentifier, e);
            }
        }

        return errors;
    }

    private void validateStudent(StudentCsvDto studentDto) {
        if (userRepository.existsByEmail(studentDto.getEmail())) {
            throw new DataValidationException("User with email " + studentDto.getEmail() + " already exists.");
        }
    }

    private List<User> processAllStudents(List<StudentCsvDto> students, Map<String, Country> countriesMap) {
        List<User> processedUsers = new ArrayList<>();

        for (StudentCsvDto studentDto : students) {
            User user = createUserFromStudent(studentDto, countriesMap);
            processedUsers.add(user);
        }

        return processedUsers;
    }

    private String getStudentIdentifier(StudentCsvDto studentDto) {
        return studentDto.getStudentID() != null && !studentDto.getStudentID().trim().isEmpty()
                ? studentDto.getStudentID()
                : studentDto.getEmail();
    }

    private User createUserFromStudent(StudentCsvDto studentDto, Map<String, Country> countriesMap) {
        User user = csvUserMapper.toUser(studentDto);
        user.setPassword(PasswordGenerator.generatePassword());

        Country country = getCountry(studentDto.getCountry(), countriesMap);
        user.setCountry(country);

        User savedUser = userRepository.save(user);

        Education education = csvUserMapper.toEducation(studentDto);
        education.setYearFrom(studentDto.getAdmissionDate() != null ? studentDto.getAdmissionDate().getYear() : null);
        education.setYearTo(studentDto.getGraduationDate() != null ? studentDto.getGraduationDate().getYear() : null);
        education.setUser(savedUser);
        educationRepository.save(education);

        return savedUser;
    }

    private CsvUploadResponseDto buildSuccessResponse(int totalStudents, int processedCount) {
        CsvUploadResponseDto response = new CsvUploadResponseDto();
        response.setTotalStudents(totalStudents);
        response.setProcessedCount(processedCount);
        response.setErrors(new ArrayList<>());
        response.setErrorCount(0);
        return response;
    }

    private Map<String, Country> getAllCountriesMap() {
        List<Country> countries = new ArrayList<>();
        countryRepository.findAll().forEach(countries::add);

        return countries.stream()
                .collect(Collectors.toMap(
                        Country::getTitle,
                        Function.identity(),
                        (existingCountry, newCountry) -> existingCountry));
    }

    private Country getCountry(String countryName, Map<String, Country> countriesMap) {
        Country existingCountry = countriesMap.get(countryName);
        if (existingCountry != null) {
            return existingCountry;
        }
        return createCountry(countryName, countriesMap);
    }

    private Country createCountry(String countryName, Map<String, Country> countriesMap) {
        Country newCountry = new Country();
        newCountry.setTitle(countryName);

        Country savedCountry = countryRepository.save(newCountry);
        countriesMap.put(countryName, savedCountry);

        log.info("Created new country: {}", savedCountry.getTitle());
        return savedCountry;
    }
}