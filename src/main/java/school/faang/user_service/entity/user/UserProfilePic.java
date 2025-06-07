package school.faang.user_service.entity.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.resource.Resource;

@Getter
@Setter
@Embeddable
public class UserProfilePic {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_pic_file_id")
    private Resource file;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_pic_small_file_id")
    private Resource smallFile;
}