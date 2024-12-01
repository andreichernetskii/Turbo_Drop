package common_jpa.dao;

import common_jpa.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
