package io.rosenwald.springDemo.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.rosenwald.springDemo.entities.Vehicle;

/**
 * A JPA Repository that reads, writes, and modifies data to and from a SQL database containing vehicle data.
 * 
 * {@link #findAllYears()}, {@link #findByYearAndMake(int, String)} and 
 * {@link #findByYearAndMakeAndModel(int, String, String)} all use custom SQL/SpEL queries.
 * 
 * @author Nathaniel Rosenwald
 *
 */
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
	public List<Vehicle> findByYear(int year);
	public List<Vehicle> findByMake(String make);
	public List<Vehicle> findByModel(String model);

	@Query(value = "SELECT DISTINCT v.year FROM #{#entityName} v ORDER BY year ASC")
	public List<Integer> findAllYears();
	
	@Query(value = "SELECT v FROM #{#entityName} v WHERE v.year=:year AND v.make=:make")
	public List<Vehicle> findByYearAndMake(@Param("year") int year, @Param("make") String make);
	
	@Query(value = "SELECT v FROM #{#entityName} v WHERE v.year=:year AND v.make=:make AND v.model=:model")
	public List<Vehicle> findByYearAndMakeAndModel(@Param("year") int year, @Param("make") String make, @Param("model") String model);
}
