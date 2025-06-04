package school.faang.user_service.service.user_service;

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
import school.faang.user_service.mapper.CsvUserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

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
@Transactional
public class UserServiceImpl implements UserService {
    private final CsvMapper csvMapper;
    private final CsvUserMapper csvUserMapper;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final EducationRepository educationRepository;
    private final PasswordGenerator passwordGenerator;

    @Override
    public CsvUploadResponseDto processStudentsCsv(MultipartFile file) {
        try {
            List<StudentCsvDto> students = readCsvFile(file.getInputStream());
            Map<String, Country> countriesMap = getAllCountriesMap();
            List<User> processedUsers = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            for (StudentCsvDto studentDto : students) {
                try {
                    User user = processUsers(studentDto, countriesMap);
                    processedUsers.add(user);
                } catch (Exception e) {
                    errors.add("Error processing student " + studentDto.getEmail() + ": " + e.getMessage());
                    log.error("Error processing student  {}", studentDto.getEmail(), e);

                }
            }
            CsvUploadResponseDto response = new CsvUploadResponseDto();
            response.setTotalStudents(students.size());
            response.setProcessedCount(processedUsers.size());
            response.setErrors(errors);
            response.setErrorCount(errors.size());
            return response;

        } catch (IOException e) {
            throw new DataValidationException("Error reading CSV file" + e.getMessage());
        }
    }

    private List<StudentCsvDto> readCsvFile(InputStream inputStream) {
        CsvSchema schema = csvMapper.schemaFor(StudentCsvDto.class)
                .withColumnSeparator(',')
                .withLineSeparator("\n")
                .withHeader();
        MappingIterator<StudentCsvDto> iterator;
        try {
            iterator = csvMapper
                    .readerFor(StudentCsvDto.class)
                    .with(schema)
                    .readValues(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return iterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private User processUsers(StudentCsvDto studentDto, Map<String, Country> countriesMap) {
        if (userRepository.existsByEmail(studentDto.getEmail())) {
            throw new DataValidationException("User with email " + studentDto.getEmail() + " already exists.");
        }
        User user = csvUserMapper.toUser(studentDto);
        user.setPassword(passwordGenerator.generatePassword());
        Country country = getOrCreateCountry(studentDto.getCountry(), countriesMap);
        user.setCountry(country);
        User savedUser = userRepository.save(user);
        Education education = csvUserMapper.toEducation(studentDto);
        education.setUser(savedUser);
        educationRepository.save(education);
        return savedUser;
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

    private Country getOrCreateCountry(String countryName, Map<String, Country> countriesMap) {
        return countriesMap.computeIfAbsent(countryName, name -> {
            Country newCountry = new Country();
            newCountry.setTitle(name);

            Country savedCountry = countryRepository.save(newCountry);
            log.info("Created new country {}", savedCountry);
            return savedCountry;
        });
    }
}
