package school.faang.user_service.controller.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.contact.RegisterTelegramDto;
import school.faang.user_service.service.ContactService;
import school.faang.user_service.utils.Utils;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {
    public static final String CHAT_ID = "1";
    public static final String PHONE = "1234567890";

    private final Utils utils = new Utils();
    private MockMvc mockMvc;
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private ContactService contactService;
    @InjectMocks
    private ContactController contactController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
    }

    @Test
    public void testRegisterTelegramChatIdSuccess() throws Exception {
        RegisterTelegramDto requestDto = RegisterTelegramDto.builder()
            .chatId(CHAT_ID)
            .phone(PHONE)
            .build();

        doNothing().when(contactService).registerTelegramChatId(requestDto);

        mockMvc.perform(post("/contacts/telegram")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
            )
            // .andDo(print())
            .andExpect(status().is(HttpStatus.CREATED.value()))
            .andExpect(content().string(""));
    }

    @Test
    public void testUnregisterTelegramChatId() throws Exception {
        String url = utils.format("/contacts/telegram/{}", CHAT_ID);

        mockMvc.perform(delete(url))
            // .andDo(print())
            .andExpect(status().is(HttpStatus.NO_CONTENT.value()))
            .andExpect(content().string(""));
    }
}