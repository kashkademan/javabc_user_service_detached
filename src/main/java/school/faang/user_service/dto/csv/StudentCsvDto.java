package school.faang.user_service.dto.csv;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentCsvDto {
    @JsonProperty("firstName")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @JsonProperty("lastName")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @JsonProperty("yearOfBirth")
    @Min(value = 1900, message = "Year of birth must be at least 1900")
    private Integer yearOfBirth;

    @JsonProperty("group")
    @NotBlank(message = "Group cannot be blank")
    private String group;

    @JsonProperty("studentID")
    @NotBlank(message = "Student ID cannot be blank")
    private String studentID;

    @JsonProperty("email")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @JsonProperty("phone")
    @Pattern(message = "Phone number must be a valid phone number", regexp = "^\\+?[0-9]{10,13}$")
    private String phone;

    @JsonProperty("street")
    @NotBlank(message = "Street cannot be blank")
    private String street;

    @JsonProperty("city")
    @NotBlank(message = "City cannot be blank")
    private String city;

    @JsonProperty("state")
    @NotBlank(message = "State cannot be blank")
    private String state;

    @JsonProperty("country")
    @NotBlank(message = "Country cannot be blank")
    private String country;

    @JsonProperty("postalCode")
    @NotBlank(message = "Postal code cannot be blank")
    private String postalCode;

    @JsonProperty("faculty")
    @NotBlank(message = "Faculty cannot be blank")
    private String faculty;

    @JsonProperty("institution")
    @NotBlank(message = "institution cannot be blank")
    private String institution;

    @JsonProperty("completionYear")
    @Min(value = 1900, message = "Completion year must be at least 1900")
    private Integer completionYear;

    @JsonProperty("employer")
    private String employer;

    @JsonProperty("scholarship")
    private Boolean scholarship;

    @JsonProperty("admissionDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate admissionDate;

    @JsonProperty("graduationDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    @JsonProperty("status")
    @NotBlank(message = "Status cannot be blank")
    private String status;

    @JsonProperty("GPA")
    @DecimalMin(value = "0.0", message = "GPA must be at least 0.0")
    @DecimalMax(value = "4.0", message = "GPA must be at most 4.0")
    private Double GPA;

    @JsonProperty("yearOfStudy")
    @Min(value = 1, message = "Year of study must be at least 1")
    private Integer yearOfStudy;

    @JsonProperty("major")
    @NotBlank(message = "Major cannot be blank")
    private String major;

    @JsonProperty("degree")
    @NotBlank(message = "Degree cannot be blank")
    private String degree;
}