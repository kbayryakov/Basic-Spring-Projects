package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entity.Volcanologist;

import java.util.Optional;

public interface VolcanologistRepository extends JpaRepository<Volcanologist, Long> {


    Optional<Volcanologist> findByFirstNameAndLastName(String firstName, String lastName);
}
