package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvatarGenerationServiceImpl implements AvatarGenerationService {
    private final Random random = new Random();
    private final List<String> flipVariants = List.of(
            "true",
            "false"
    );
    private final List<String> backgroundColorVariants = List.of(
            "b6e3f4",
            "c0aede",
            "d1d4f9",
            "ffd5dc",
            "ffdfbf"
    );
    private final List<String> eyesVariants = List.of(
            "cheery",
            "normal",
            "confused",
            "starstruck",
            "winking",
            "sleepy",
            "sad",
            "angry"
    );
    private final List<String> hairVariants = List.of(
            "shortHair",
            "mohawk",
            "wavyBob",
            "bowlCutHair",
            "curlyBob",
            "straightHair",
            "braids",
            "shavedHead",
            "bunHair",
            "froBun",
            "bangs",
            "halfShavedHead",
            "curlyShortHair"
    );
    private final List<String> hairColorVariants = List.of(
            "220f00",
            "3a1a00",
            "71472d",
            "e2ba87",
            "605de4",
            "238d80",
            "d56c0c",
            "e9b729"
    );
    private final List<String> moutheVariants = List.of(
            "openedSmile",
            "unimpressed",
            "gapSmile",
            "openSad",
            "teethSmile",
            "awkwardSmile",
            "braces",
            "kawaii"
    );
    private final List<String> skinColorVariants = List.of(
            "ffe4c0",
            "f5d7b1",
            "efcc9f",
            "e2ba87",
            "c99c62",
            "a47539",
            "8c5a2b",
            "643d19"
    );

    @Override
    public String generateAvatarUrl() {
        String generatedAvatarUrl = "https://api.dicebear.com/9.x/big-smile/svg";
        int randomElementIndex = random.nextInt(flipVariants.size());
        generatedAvatarUrl += "?flip=" + flipVariants.get(randomElementIndex);
        randomElementIndex = random.nextInt(backgroundColorVariants.size());
        generatedAvatarUrl += "&backgroundColor=" + backgroundColorVariants.get(randomElementIndex);
        randomElementIndex = random.nextInt(eyesVariants.size());
        generatedAvatarUrl += "&eyes=" + eyesVariants.get(randomElementIndex);
        randomElementIndex = random.nextInt(hairVariants.size());
        generatedAvatarUrl += "&hair=" + hairVariants.get(randomElementIndex);
        randomElementIndex = random.nextInt(hairColorVariants.size());
        generatedAvatarUrl += "&hairColor=" + hairColorVariants.get(randomElementIndex);
        randomElementIndex = random.nextInt(moutheVariants.size());
        generatedAvatarUrl += "&mouthe=" + moutheVariants.get(randomElementIndex);
        randomElementIndex = random.nextInt(skinColorVariants.size());
        return generatedAvatarUrl + "&skinColor=" + skinColorVariants.get(randomElementIndex);
    }

    @Override
    public String setSizeToGeneratedAvatar(String generatedAvatarUrl, int requiredSize) {
        return generatedAvatarUrl + "&size=" + requiredSize;
    }
}
