package backend.time.repository;

import backend.time.model.Objection.Objection;
import backend.time.model.Objection.ObjectionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjectionImageRepository extends JpaRepository<ObjectionImage,Long> {
    List<ObjectionImage> findByObjection(Objection objection);

}
