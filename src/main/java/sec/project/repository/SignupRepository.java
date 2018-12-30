package sec.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sec.project.domain.Signup;

public interface SignupRepository extends JpaRepository<Signup, Long> {

    @Query(value = "SELECT * FROM Signup WHERE name = ?", nativeQuery = true)
    List<Signup> findByUsername(String name);
    
    List<Signup> findByName(String name);
    
}
