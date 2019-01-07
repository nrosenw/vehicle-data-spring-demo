package io.rosenwald.springDemo.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.rosenwald.springDemo.DemoApplication;
import io.rosenwald.springDemo.db.VehicleRepository;
import io.rosenwald.springDemo.entities.Vehicle;

/**
 * A controller providing the REST endpoints and their querying logic. Retrieves its vehicle data from the 
 * {@link io.rosenwald.springDemo.db.VehicleRepository}.
 * 
 * TODO: Provide better error handling/input validation with different response statuses.
 * 
 * @author Nathaniel Rosenwald
 *
 */
@RestController
public class VehicleRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	@Autowired VehicleRepository repo;
	
	private static final String NULL = "null";
	
	/**
	 * Queries for a list of vehicles based on the properties provided. 
	 * 
	 * @param year The model year of a vehicle.
	 * @param make The make of a vehicle.
	 * @param model The model of a vehicle.
	 * @return A list of matching vehicles. Provided as a JSON array to the client of the REST endpoint.
	 */
	@GetMapping("/vehicles")
	public ResponseEntity<List<Vehicle>> getVehicles(@RequestParam(value="year", defaultValue=NULL) String year, 
			@RequestParam(value="make", defaultValue=NULL) String make, 
			@RequestParam(value="model", defaultValue=NULL) String model) {
		Integer numericYear;
		if (year == null || NULL.equals(year)) {
			numericYear = null;
		} else {
			try {
				numericYear = NumberUtils.parseNumber(year, Integer.class);
			} catch (Exception ex) {
				return new ResponseEntity<List<Vehicle>>(new ArrayList<Vehicle>(), HttpStatus.OK);
			}
		}
		
		try {
			if (numericYear != null && make.equals(NULL) && model.equals(NULL)) {
				return new ResponseEntity<List<Vehicle>>(repo.findByYear(numericYear), HttpStatus.OK);
			} else if (numericYear == null && !make.equals(NULL) && model.equals(NULL)) {
				return new ResponseEntity<List<Vehicle>>(repo.findByMake(make), HttpStatus.OK);
			} else if (numericYear == null && make.equals(NULL) && !model.equals(NULL)) {
				return new ResponseEntity<List<Vehicle>>(repo.findByModel(model), HttpStatus.OK);
			} else if (numericYear != null && !make.equals(NULL) && model.equals(NULL)) {
				return new ResponseEntity<List<Vehicle>>(repo.findByYearAndMake(numericYear, make), HttpStatus.OK);
			} else if (numericYear == null && make.equals(NULL) && model.equals(NULL)) {
				return new ResponseEntity<List<Vehicle>>(repo.findAll(), HttpStatus.OK);
			}
			
			return new ResponseEntity<List<Vehicle>>(repo.findByYearAndMakeAndModel(numericYear, make, model), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
			return new ResponseEntity<List<Vehicle>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	/**
//	 * Queries for a vehicle based on it's ID. Commented out because it does not fit the data model well.
//	 * @param id The UUID of the vehicle.
//	 * @return The matching vehicle.
//	 */
//	@GetMapping("/vehicles/{$id}")
//	public Vehicle getVehiclesById(@PathVariable String id) {
//		return repo.findById(id).orElse(null);
//	}
	
	/**
	 * Queries for vehicles based on their model year. 
	 * @param id The model year of the vehicles.
	 * @return A list of matching vehicles.
	 */
	@GetMapping("/vehicles/{year}")
	public ResponseEntity<List<Vehicle>> getVehiclesByYear(@PathVariable int year) {
		try {
			return new ResponseEntity<List<Vehicle>>(repo.findByYear(year), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
			return new ResponseEntity<List<Vehicle>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Queries for vehicles based on their model year and make. 
	 * @param id The make of the vehicles.
	 * @return A list of matching vehicles.
	 */
	@GetMapping("/vehicles/{year}/{make}")
	public ResponseEntity<List<Vehicle>> getVehiclesByYearAndMake(@PathVariable int year, @PathVariable String make) {
		try {
			return new ResponseEntity<List<Vehicle>>(repo.findByYearAndMake(year, make), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
			return new ResponseEntity<List<Vehicle>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Queries for vehicles based on their model year, make, and ymodel. 
	 * @param id The model of the vehicles.
	 * @return A list of matching vehicles.
	 */
	@GetMapping("/vehicles/{year}/{make}/{model}")
	public ResponseEntity<List<Vehicle>> getVehiclesByYearAndMakeAndModel(@PathVariable int year, @PathVariable String make, @PathVariable String model) {
		try {
			return new ResponseEntity<List<Vehicle>>(repo.findByYearAndMakeAndModel(year, make, model), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
			return new ResponseEntity<List<Vehicle>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Queries for all available model years. 
	 * @return A list of integers all representing a model year.
	 */
	@GetMapping("/modelYears")
	public ResponseEntity<List<Integer>> getAllModelYears() {
		try {
			return new ResponseEntity<List<Integer>>(repo.findAllYears(), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
			return new ResponseEntity<List<Integer>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Saves a vehicle to the repository.
	 * @param vehicle The vehicle to save.
	 * @return The vehicle that was saved.
	 */
	@PostMapping("/vehicles")
	public ResponseEntity<Vehicle> postVehicle(@RequestBody Vehicle vehicle) {
		try {
			return new ResponseEntity<Vehicle>(repo.save(vehicle), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error(ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
			return new ResponseEntity<Vehicle>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Deletes a vehicle from the repository based on it's UUID.
	 * @param id The UUID of the vehicle to be deleted.
	 * @return The deleted vehicle.
	 */
	@DeleteMapping("/vehicles/{id}")
	public ResponseEntity<Vehicle> deleteVehicle(@PathVariable String id) {
		Optional<Vehicle> vehicle = repo.findById(id);
		if (vehicle.isPresent()) {
			repo.deleteById(id);
			return new ResponseEntity<Vehicle>(vehicle.get(), HttpStatus.OK);
		}
		return new ResponseEntity<Vehicle>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Deletes the entire repository. Uses multi-threaded, fixed size batching; see 
	 * {@link io.rosenwald.springDemo.db.MultithreadedRepositoryCommunicator}.
	 * 
	 * *This should probably not exist in a production application but I'm leaving it anyways for now.*
	 * 
	 * TODO: Provide better rollback in case of repository error. May not do this as this endpoint is not necessary at all.
	 * 
	 * @return The list of all deleted vehicles. 
	 */
	@DeleteMapping("/vehicles")
	public ResponseEntity<List<Vehicle>> deleteVehicle() {
		List<Vehicle> vehicles = repo.findAll();
		repo.deleteAll();
		if (repo.count() != 0) {
			repo.saveAll(vehicles);
			return new ResponseEntity<List<Vehicle>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Vehicle>>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
